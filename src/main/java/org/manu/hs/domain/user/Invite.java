package org.manu.hs.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * Permission to sign in while sign-in is invite-only: a username at a login provider (GitHub),
 * matched in any case. While there are none, anyone may sign in.
 */
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
public class Invite {

    private Long id;
    private AuthProvider provider;
    private String providerLogin;
    /** Who added it; null once that account is gone. */
    private Long invitedBy;
    private Instant invitedAt;

}
