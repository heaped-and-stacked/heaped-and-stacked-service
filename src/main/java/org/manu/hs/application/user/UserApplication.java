package org.manu.hs.application.user;

import lombok.RequiredArgsConstructor;
import org.manu.hs.application.common.CurrentUserProvider;
import org.manu.hs.application.common.exception.InvalidFieldValueException;
import org.manu.hs.application.user.command.SignInCommand;
import org.manu.hs.application.user.command.UpdateProfileCommand;
import org.manu.hs.application.user.exception.AdminFromConfigurationException;
import org.manu.hs.application.user.exception.SignInNotInvitedException;
import org.manu.hs.application.user.exception.UserNotActiveException;
import org.manu.hs.domain.user.Invite;
import org.manu.hs.domain.user.Overview;
import org.manu.hs.domain.user.User;
import org.manu.hs.domain.user.UserIdentity;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.domain.user.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.manu.hs.application.common.Validations.requireNonNull;

/**
 * Entry point of the user feature: sign-in, the user's own profile, and user
 * administration.
 * <p>
 * Each use case checks the caller's permissions and runs the steps in the agreed order
 * (404, then 403, then 400, then 409). It is the only place that reads the current
 * user; UserService receives ids and the time and holds the rules and persistence.
 * <p>
 * Sign-ins and changes to people (role, status, deletion) are written to the "audit" log: one
 * line per action, ids only.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserApplication {

    private static final Logger AUDIT = LoggerFactory.getLogger("audit");

    private final UserService userService;
    private final CurrentUserProvider userProvider;

    // ---------------------------------------------------------------------
    // Sign-in (called by the login flow, not by an endpoint)
    // ---------------------------------------------------------------------

    /**
     * Finds the user behind a provider account, or registers them on their first
     * sign-in. Bootstrap admins are made ADMIN on every sign-in; blocked and deleted
     * accounts are refused.
     */
    public User signIn(SignInCommand command) {
        userService.validate(command);

        final Instant now = Instant.now();
        final boolean bootstrapAdmin = userService.isBootstrapAdmin(command.getProvider(), command.getProviderUserId());
        try {
            userService.requireInvited(command, bootstrapAdmin);
        } catch (SignInNotInvitedException refused) {
            AUDIT.info("sign_in_refused reason=not_invited");
            throw refused;
        }
        final Optional<UserIdentity> identity = userService.findIdentity(command.getProvider(), command.getProviderUserId());

        if (identity.isEmpty()) {
            final UserRole role = bootstrapAdmin ? UserRole.ADMIN : UserRole.MEMBER;
            final User registered = userService.register(command, role, now);
            AUDIT.info("sign_in user={} role={} new=true", registered.getId(), registered.getRole());
            return registered;
        }

        final User existing = userService.findExistingOrThrow(identity.get().getUserId());
        try {
            userService.requireActive(existing);
        } catch (UserNotActiveException refused) {
            AUDIT.info("sign_in_refused user={} status={}", existing.getId(), existing.getStatus());
            throw refused;
        }

        final UserRole role = bootstrapAdmin ? UserRole.ADMIN : existing.getRole();
        final User signedIn = userService.recordSignIn(existing, identity.get(), command, role, now);
        AUDIT.info("sign_in user={} role={}", signedIn.getId(), signedIn.getRole());
        return signedIn;
    }

    // ---------------------------------------------------------------------
    // The caller's own account
    // ---------------------------------------------------------------------

    public User getUser(Long userId) {
        return userService.findExistingOrThrow(userId);
    }

    public User updateProfile(Long userId, UpdateProfileCommand command) {
        final User existing = userService.findExistingOrThrow(userId);
        userService.validate(command);

        return userService.updateProfile(existing, command);
    }

    public void deleteAccount(Long userId) {
        final User existing = userService.findExistingOrThrow(userId);
        userService.requireNotLastActiveAdmin(existing);

        userService.softDelete(existing, Instant.now());
        AUDIT.info("account_deleted user={}", userId);
    }

    // ---------------------------------------------------------------------
    // Administration
    // ---------------------------------------------------------------------

    /** The numbers at the top of the admin page: users, this week's activity, and what waits for a decision. */
    public Overview getOverview() {
        userProvider.requireAdmin("view the overview");

        return userService.countOverview(Instant.now());
    }

    public List<User> listUsers(UserRole roleFilter, UserStatus statusFilter) {
        userProvider.requireAdmin("list users");

        return userService.findAll(roleFilter, statusFilter);
    }

    /** Who may sign in while sign-in is invite-only; none means anyone may. */
    public List<Invite> getInvites() {
        userProvider.requireAdmin("list invites");

        return userService.findInvites();
    }

    public Invite invite(String login) {
        userProvider.requireAdmin("invite people");
        userService.validateInviteLogin(login);

        final Invite invite = userService.invite(login, userProvider.userId());
        AUDIT.info("invite_added by={} invite={}", userProvider.userId(), invite.getId());
        return invite;
    }

    public void deleteInvite(Long id) {
        userProvider.requireAdmin("remove invites");

        userService.deleteInvite(id);
        AUDIT.info("invite_removed by={} invite={}", userProvider.userId(), id);
    }

    public User changeRole(Long userId, UserRole newRole) {
        final User existing = userService.findExistingOrThrow(userId);
        userProvider.requireAdmin("change a user's role");
        requireNonNull(newRole, "role");
        // Admins come only from the configuration (BOOTSTRAP_ADMIN_IDS): the app neither grants
        // nor takes away the role.
        if (newRole == UserRole.ADMIN) {
            throw new InvalidFieldValueException("role", "must be MEMBER or STAFF: admins are set in the configuration");
        }
        userService.requireNotHouseAccount(existing);
        if (existing.getRole() == UserRole.ADMIN) {
            throw new AdminFromConfigurationException(userId);
        }

        final User changed = userService.changeRole(existing, newRole);
        AUDIT.info("role_changed by={} user={} role={}", userProvider.userId(), userId, newRole);
        return changed;
    }

    public User changeStatus(Long userId, UserStatus newStatus) {
        final User existing = userService.findExistingOrThrow(userId);
        userProvider.requireAdmin("change a user's status");
        userService.validateStatusChange(newStatus);

        userService.requireNotHouseAccount(existing);
        if (newStatus != UserStatus.ACTIVE) {
            userService.requireNotLastActiveAdmin(existing);
        }

        final User changed = userService.changeStatus(existing, newStatus);
        AUDIT.info("status_changed by={} user={} status={}", userProvider.userId(), userId, newStatus);
        return changed;
    }

    /**
     * An admin deletes someone's account, e.g. on an emailed erasure request or for someone who
     * can't sign in any more: the same soft delete as deleting one's own account. With
     * eraseActivity, their sets, votes and feedback go too; what they wrote stays, nameless.
     */
    public void deleteAccountAsAdmin(Long userId, boolean eraseActivity) {
        final User existing = userService.findExistingOrThrow(userId);
        userProvider.requireAdmin("delete a user's account");
        userService.requireNotHouseAccount(existing);
        userService.requireNotLastActiveAdmin(existing);

        if (eraseActivity) {
            userService.eraseActivity(existing);
        }
        userService.softDelete(existing, Instant.now());
        AUDIT.info("account_deleted by={} user={} activity_erased={}", userProvider.userId(), userId, eraseActivity);
    }
}
