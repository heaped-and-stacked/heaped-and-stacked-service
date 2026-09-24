package org.manu.hs.application.user;

import org.manu.hs.application.common.Validations;
import org.manu.hs.application.common.exception.InvalidFieldValueException;
import org.manu.hs.application.user.command.SignInCommand;
import org.manu.hs.application.user.command.UpdateProfileCommand;
import org.manu.hs.application.user.exception.HouseAccountException;
import org.manu.hs.application.user.exception.InviteExistsException;
import org.manu.hs.application.user.exception.InviteNotFoundException;
import org.manu.hs.application.user.exception.LastAdminException;
import org.manu.hs.application.user.exception.SignInNotInvitedException;
import org.manu.hs.application.user.exception.UserNotActiveException;
import org.manu.hs.application.user.exception.UserNotFoundException;
import org.manu.hs.domain.user.AuthProvider;
import org.manu.hs.domain.user.Invite;
import org.manu.hs.domain.user.Overview;
import org.manu.hs.domain.user.User;
import org.manu.hs.domain.user.UserIdentity;
import org.manu.hs.domain.user.UserRepository;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.domain.user.UserStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.manu.hs.application.common.Validations.requireNonBlank;
import static org.manu.hs.application.common.Validations.requireNonNull;
import static org.manu.hs.application.common.Validations.requireSingleLine;

/**
 * User rules, building users from commands, and user persistence.
 * Package-private: only UserApplication uses it.
 * <p>
 * Never reads the current user: the caller's id and the time are passed in, which
 * keeps every method easy to unit test.
 */
@Service
class UserService {

    private static final int MAX_DISPLAY_NAME_LENGTH = 200;
    /** A GitHub username: letters, digits and hyphens, at most 39 characters. */
    private static final Pattern GITHUB_LOGIN = Pattern.compile("[A-Za-z0-9](?:[A-Za-z0-9-]{0,38})");
    /** The account that owns the gym's content, as the content migration creates it. */
    private static final String HOUSE_ACCOUNT_NAME = "Heaped & Stacked";
    /** "This week" on the admin overview: the 7 days up to now. */
    private static final Duration WEEK = Duration.ofDays(7);

    private final UserRepository userRepository;
    private final Set<String> bootstrapAdminIds;

    /**
     * Written by hand (not @RequiredArgsConstructor) because Lombok can't carry the
     * {@code @Value} annotation onto a generated constructor parameter.
     *
     * @param bootstrapAdminIds "PROVIDER:providerUserId" entries, e.g. "GITHUB:12345678"
     */
    UserService(
            UserRepository userRepository,
            @Value("${app.security.bootstrap-admin-ids}") List<String> bootstrapAdminIds) {
        this.userRepository = userRepository;
        this.bootstrapAdminIds = bootstrapAdminIds.stream()
                .map(String::trim)
                .filter(id -> !id.isEmpty())
                .collect(Collectors.toSet());
    }

    // ---------------------------------------------------------------------
    // Validation of commands: pure, no repository access
    // ---------------------------------------------------------------------

    void validate(SignInCommand command) {
        requireNonNull(command.getProvider(), "provider");
        requireNonBlank(command.getProviderUserId(), "providerUserId");
    }

    void validate(UpdateProfileCommand command) {
        requireNonBlank(command.getDisplayName(), "displayName");
        if (command.getDisplayName().length() > MAX_DISPLAY_NAME_LENGTH) {
            throw new InvalidFieldValueException(
                    "displayName",
                    "must be at most %d characters".formatted(MAX_DISPLAY_NAME_LENGTH));
        }
        requireSingleLine(command.getDisplayName(), "displayName");
    }

    /** Admins change a user's status to ACTIVE or BLOCKED; DELETED only comes from the user. */
    void validateStatusChange(UserStatus status) {
        requireNonNull(status, "status");
        if (status == UserStatus.DELETED) {
            throw new InvalidFieldValueException("status", "must be ACTIVE or BLOCKED");
        }
    }

    // ---------------------------------------------------------------------
    // Rules and queries that need stored data or configuration
    // ---------------------------------------------------------------------

    /** A user that exists and hasn't deleted their account; deleted users count as gone. */
    User findExistingOrThrow(Long userId) {
        return userRepository.findById(userId)
                .filter(user -> user.getStatus() != UserStatus.DELETED)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    Optional<UserIdentity> findIdentity(AuthProvider provider, String providerUserId) {
        return userRepository.findIdentity(provider, providerUserId);
    }

    List<User> findAll(UserRole role, UserStatus status) {
        return userRepository.findAll(role, status);
    }

    /** Whether this provider account is configured as an admin (applied on every sign-in). */
    boolean isBootstrapAdmin(AuthProvider provider, String providerUserId) {
        return bootstrapAdminIds.contains(provider.name() + ":" + providerUserId);
    }

    void requireActive(User user) {
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UserNotActiveException(user.getId(), user.getStatus());
        }
    }

    // ---------------------------------------------------------------------
    // Invites: while there are any, only invited accounts sign in (and bootstrap admins)
    // ---------------------------------------------------------------------

    void requireInvited(SignInCommand command, boolean bootstrapAdmin) {
        if (bootstrapAdmin || !userRepository.hasInvites()) {
            return;
        }
        final String login = command.getProviderLogin();
        if (login == null || !userRepository.isInvited(command.getProvider(), login)) {
            throw new SignInNotInvitedException(login);
        }
    }

    void validateInviteLogin(String login) {
        requireNonBlank(login, "login");
        if (!GITHUB_LOGIN.matcher(login.strip()).matches()) {
            throw new InvalidFieldValueException("login", "must be a GitHub username");
        }
    }

    List<Invite> findInvites() {
        return userRepository.findInvites();
    }

    Invite invite(String login, Long invitedBy) {
        final String username = login.strip();
        if (userRepository.isInvited(AuthProvider.GITHUB, username)) {
            throw new InviteExistsException(username);
        }
        return userRepository.insertInvite(Invite.builder()
                .provider(AuthProvider.GITHUB)
                .providerLogin(username)
                .invitedBy(invitedBy)
                .build());
    }

    void deleteInvite(Long id) {
        if (!userRepository.deleteInvite(id)) {
            throw new InviteNotFoundException(id);
        }
    }

    /**
     * Refuses to demote, block or delete the only remaining active admin.
     * Only called for changes that would take away the user's active-admin standing.
     */
    void requireNotLastActiveAdmin(User user) {
        final boolean activeAdmin = user.getRole() == UserRole.ADMIN && user.getStatus() == UserStatus.ACTIVE;
        if (activeAdmin && userRepository.countActiveAdminsLocking() <= 1) {
            throw new LastAdminException(user.getId());
        }
    }

    /** The admin overview's numbers; "this week" is the 7 days up to now, and the house account isn't a user. */
    Overview countOverview(Instant now) {
        return userRepository.countOverview(now.minus(WEEK), HOUSE_ACCOUNT_NAME);
    }

    /**
     * Refuses to delete, block or change the role of the house account, which owns the gym's
     * content: the account the content migration creates under HOUSE_ACCOUNT_NAME, with no
     * sign-in linked. (A member who renames themselves "Heaped & Stacked" still has their GitHub
     * login, so they aren't mistaken for it.)
     */
    void requireNotHouseAccount(User user) {
        if (HOUSE_ACCOUNT_NAME.equals(user.getDisplayName()) && !userRepository.hasIdentity(user.getId())) {
            throw new HouseAccountException(user.getId());
        }
    }

    // ---------------------------------------------------------------------
    // Building and persistence
    // ---------------------------------------------------------------------

    /** First sign-in: creates the user and links the provider account to it. */
    User register(SignInCommand command, UserRole role, Instant now) {
        final User user = userRepository.insert(newUser(command, role, now));
        userRepository.insertIdentity(newIdentity(user.getId(), command, now));
        return user;
    }

    /** Returning sign-in: refreshes what may have changed at the provider. */
    User recordSignIn(User user, UserIdentity identity, SignInCommand command, UserRole role, Instant now) {
        userRepository.updateIdentity(signedInIdentity(identity, command, now));
        return userRepository.update(signedInUser(user, command, role, now));
    }

    User updateProfile(User user, UpdateProfileCommand command) {
        return userRepository.update(user.toBuilder()
                .displayName(command.getDisplayName())
                .build());
    }

    User changeRole(User user, UserRole role) {
        return userRepository.update(user.toBuilder()
                .role(role)
                .build());
    }

    User changeStatus(User user, UserStatus status) {
        return userRepository.update(user.toBuilder()
                .status(status)
                .build());
    }

    /**
     * Soft delete: the row stays so content keeps a valid author, personal data is
     * cleared, and the sign-in identities are removed, so signing in again creates
     * a new account.
     */
    void softDelete(User user, Instant now) {
        userRepository.deleteIdentitiesOf(user.getId());
        userRepository.update(deletedUser(user, now));
    }

    /** Erases the user's sets, votes and feedback, on an erasure request. */
    void eraseActivity(User user) {
        userRepository.eraseActivityOf(user.getId());
    }

    /** Pure: the user to create on a first sign-in. */
    User newUser(SignInCommand command, UserRole role, Instant now) {
        return User.builder()
                .displayName(isValidDisplayName(command.getDisplayName()) ? command.getDisplayName() : command.getProviderLogin())
                .avatarUrl(command.getAvatarUrl())
                .role(role)
                .status(UserStatus.ACTIVE)
                .lastLoginAt(now)
                .build();
    }

    /**
     * Whether a name follows the rules members' own names follow (validate(UpdateProfileCommand)):
     * a provider's name that doesn't is replaced by the login, so every display name follows them.
     */
    private static boolean isValidDisplayName(String name) {
        return name != null && !name.isBlank() && name.length() <= MAX_DISPLAY_NAME_LENGTH && Validations.isSingleLine(name);
    }

    /** Pure: the provider account to link to a new user. */
    UserIdentity newIdentity(Long userId, SignInCommand command, Instant now) {
        return UserIdentity.builder()
                .userId(userId)
                .provider(command.getProvider())
                .providerUserId(command.getProviderUserId())
                .providerLogin(command.getProviderLogin())
                .lastLoginAt(now)
                .build();
    }

    /**
     * Pure: a returning user. The avatar follows the provider; the display name does
     * not, because the user may have changed it here (PUT /users/me).
     */
    User signedInUser(User user, SignInCommand command, UserRole role, Instant now) {
        return user.toBuilder()
                .avatarUrl(command.getAvatarUrl())
                .role(role)
                .lastLoginAt(now)
                .build();
    }

    /** Pure: a returning provider account; its username may have changed at the provider. */
    UserIdentity signedInIdentity(UserIdentity identity, SignInCommand command, Instant now) {
        return identity.toBuilder()
                .providerLogin(command.getProviderLogin())
                .lastLoginAt(now)
                .build();
    }

    /** Pure: a deleted user, with personal data cleared. */
    User deletedUser(User user, Instant now) {
        return user.toBuilder()
                .displayName(null)
                .avatarUrl(null)
                .status(UserStatus.DELETED)
                .deletedAt(now)
                .build();
    }
}
