package org.manu.hs.application.user;

import lombok.RequiredArgsConstructor;
import org.manu.hs.application.common.CurrentUserProvider;
import org.manu.hs.application.user.command.SignInCommand;
import org.manu.hs.application.user.command.UpdateProfileCommand;
import org.manu.hs.domain.user.User;
import org.manu.hs.domain.user.UserIdentity;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.domain.user.UserStatus;
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
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserApplication {

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
        final Optional<UserIdentity> identity = userService.findIdentity(command.getProvider(), command.getProviderUserId());

        if (identity.isEmpty()) {
            final UserRole role = bootstrapAdmin ? UserRole.ADMIN : UserRole.MEMBER;
            return userService.register(command, role, now);
        }

        final User existing = userService.findExistingOrThrow(identity.get().getUserId());
        userService.requireActive(existing);

        final UserRole role = bootstrapAdmin ? UserRole.ADMIN : existing.getRole();
        return userService.recordSignIn(existing, identity.get(), command, role, now);
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
    }

    // ---------------------------------------------------------------------
    // Administration
    // ---------------------------------------------------------------------

    public List<User> listUsers(UserRole roleFilter, UserStatus statusFilter) {
        userProvider.requireAdmin("list users");

        return userService.findAll(roleFilter, statusFilter);
    }

    public User getUserAsAdmin(Long userId) {
        final User existing = userService.findExistingOrThrow(userId);
        userProvider.requireAdmin("view another user");

        return existing;
    }

    public User changeRole(Long userId, UserRole newRole) {
        final User existing = userService.findExistingOrThrow(userId);
        userProvider.requireAdmin("change a user's role");
        requireNonNull(newRole, "role");

        if (newRole != UserRole.ADMIN) {
            userService.requireNotLastActiveAdmin(existing);
        }

        return userService.changeRole(existing, newRole);
    }

    public User changeStatus(Long userId, UserStatus newStatus) {
        final User existing = userService.findExistingOrThrow(userId);
        userProvider.requireAdmin("change a user's status");
        userService.validateStatusChange(newStatus);

        if (newStatus != UserStatus.ACTIVE) {
            userService.requireNotLastActiveAdmin(existing);
        }

        return userService.changeStatus(existing, newStatus);
    }
}
