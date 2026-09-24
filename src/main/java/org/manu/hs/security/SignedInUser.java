package org.manu.hs.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.manu.hs.domain.user.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * The signed-in user as stored in the session: our own user id and role, whatever
 * provider they signed in with. Serializable because sessions are stored in Postgres.
 * <p>
 * The role is the one at sign-in time; a role or status change ends the user's
 * sessions, so the next sign-in picks up the new role.
 */
@AllArgsConstructor
@Getter
public class SignedInUser implements OAuth2User, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long userId;
    private final UserRole role;

    /** Spring Security's name for the principal; also the key Spring Session indexes sessions by. */
    @Override
    public String getName() {
        return String.valueOf(userId);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /** Kept minimal on purpose: provider data isn't needed after sign-in. */
    @Override
    public Map<String, Object> getAttributes() {
        return Map.of("userId", userId);
    }
}
