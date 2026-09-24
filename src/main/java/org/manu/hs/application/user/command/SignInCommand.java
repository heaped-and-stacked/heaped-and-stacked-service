package org.manu.hs.application.user.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.manu.hs.domain.user.AuthProvider;

/**
 * What a login provider tells us about the account that just signed in.
 */
@AllArgsConstructor
@Builder
@Getter
public class SignInCommand {

    private AuthProvider provider;
    private String providerUserId;
    private String providerLogin;
    private String displayName;
    private String avatarUrl;
}
