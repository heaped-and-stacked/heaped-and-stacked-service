package org.manu.hs.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * A person using the platform. How they sign in is kept separately, in UserIdentity.
 */
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
public class User {

    private Long id;
    private String displayName;
    private String avatarUrl;
    private UserRole role;
    private UserStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastLoginAt;
    private Instant deletedAt;

}
