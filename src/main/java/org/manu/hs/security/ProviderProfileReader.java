package org.manu.hs.security;

import org.manu.hs.application.user.command.SignInCommand;
import org.manu.hs.domain.user.AuthProvider;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * Turns one login provider's profile into a SignInCommand. Every provider names its
 * profile fields differently; adding a provider means adding one implementation.
 */
public interface ProviderProfileReader {

    AuthProvider provider();

    SignInCommand read(OAuth2User providerUser);
}
