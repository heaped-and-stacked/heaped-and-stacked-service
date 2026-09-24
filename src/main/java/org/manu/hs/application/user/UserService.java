package org.manu.hs.application.user;

import org.manu.hs.application.common.exception.InvalidFieldValueException;
import org.manu.hs.application.user.command.SignInCommand;
import org.manu.hs.application.user.command.UpdateProfileCommand;
import org.manu.hs.application.user.exception.LastAdminException;
import org.manu.hs.application.user.exception.UserNotActiveException;
import org.manu.hs.application.user.exception.UserNotFoundException;
import org.manu.hs.domain.user.AuthProvider;
import org.manu.hs.domain.user.User;
import org.manu.hs.domain.user.UserIdentity;
import org.manu.hs.domain.user.UserRepository;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.domain.user.UserStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.manu.hs.application.common.Validations.requireNonBlank;
import static org.manu.hs.application.common.Validations.requireNonNull;

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

    /**
     * Refuses to demote, block or delete the only remaining active admin.
     * Only called for changes that would take away the user's active-admin standing.
     */
    void requireNotLastActiveAdmin(User user) {
        final boolean activeAdmin = user.getRole() == UserRole.ADMIN && user.getStatus() == UserStatus.ACTIVE;
        if (activeAdmin && userRepository.countActiveAdmins() <= 1) {
            throw new LastAdminException(user.getId());
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

    /** Pure: the user to create on a first sign-in. */
    User newUser(SignInCommand command, UserRole role, Instant now) {
        return User.builder()
                .displayName(command.getDisplayName() != null ? command.getDisplayName() : command.getProviderLogin())
                .avatarUrl(command.getAvatarUrl())
                .role(role)
                .status(UserStatus.ACTIVE)
                .lastLoginAt(now)
                .build();
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
