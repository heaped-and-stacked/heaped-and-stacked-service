package org.manu.hs.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * One way a user signs in: an account at a login provider, linked to a User.
 * providerUserId is the provider's stable id for the account (GitHub: the numeric id);
 * providerLogin is the provider's username, which can change.
 */
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
public class UserIdentity {

    private Long id;
    private Long userId;
    private AuthProvider provider;
    private String providerUserId;
    private String providerLogin;
    private Instant createdAt;
    private Instant lastLoginAt;

}
