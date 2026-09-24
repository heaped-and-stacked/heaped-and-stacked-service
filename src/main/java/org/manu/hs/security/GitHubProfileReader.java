package org.manu.hs.security;

import org.manu.hs.application.user.command.SignInCommand;
import org.manu.hs.domain.user.AuthProvider;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * Reads GitHub's user profile (https://docs.github.com/en/rest/users/users#get-the-authenticated-user).
 * "id" is GitHub's stable numeric id; "login" is the username, which can change.
 */
@Component
public class GitHubProfileReader implements ProviderProfileReader {

    @Override
    public AuthProvider provider() {
        return AuthProvider.GITHUB;
    }

    @Override
    public SignInCommand read(OAuth2User providerUser) {
        final Map<String, Object> attributes = providerUser.getAttributes();

        return SignInCommand.builder()
                .provider(AuthProvider.GITHUB)
                .providerUserId(Objects.toString(attributes.get("id"), null))
                .providerLogin(Objects.toString(attributes.get("login"), null))
                .displayName(Objects.toString(attributes.get("name"), null))
                .avatarUrl(Objects.toString(attributes.get("avatar_url"), null))
                .build();
    }
}
