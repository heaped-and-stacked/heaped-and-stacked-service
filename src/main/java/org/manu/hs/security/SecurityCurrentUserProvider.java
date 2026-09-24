package org.manu.hs.security;

import org.manu.hs.application.common.CurrentUserProvider;
import org.manu.hs.domain.user.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Who is calling, taken from the signed-in session. A visitor (not signed in) has no
 * user id and is neither staff nor an admin.
 */
@Component
public class SecurityCurrentUserProvider implements CurrentUserProvider {

    @Override
    public Long userId() {
        return signedInUser()
                .map(SignedInUser::getUserId)
                .orElse(null);
    }

    @Override
    public boolean isAdmin() {
        return signedInUser()
                .map(user -> user.getRole() == UserRole.ADMIN)
                .orElse(false);
    }

    @Override
    public boolean isStaff() {
        return signedInUser()
                .map(user -> user.getRole().curatesAllContent())
                .orElse(false);
    }

    private Optional<SignedInUser> signedInUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SignedInUser user) {
            return Optional.of(user);
        }
        return Optional.empty();
    }
}
