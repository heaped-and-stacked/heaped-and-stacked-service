package org.manu.hs.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.application.user.command.SignInCommand;
import org.manu.hs.domain.user.AuthProvider;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit test for GitHubProfileReader: turning GitHub's profile (as Spring Security
 * receives it) into a SignInCommand. No Spring, no GitHub.
 */
class GitHubProfileReaderTest {

    private final GitHubProfileReader reader = new GitHubProfileReader();

    @Test
    @DisplayName("""
            Given a GitHub profile with a numeric id, username, name and avatar
            When it is read
            Then the sign-in carries them, with the id as text, for the GITHUB provider
            """)
    void readMapsGitHubProfileToSignIn() {
        // Given
        OAuth2User gitHubUser = gitHubUser(Map.of(
                "id", 12345678,
                "login", "iron-coder",
                "name", "Iron Coder",
                "avatar_url", "https://avatars.githubusercontent.com/u/12345678"));

        // When
        SignInCommand command = reader.read(gitHubUser);

        // Then
        assertEquals(AuthProvider.GITHUB, command.getProvider());
        assertEquals("12345678", command.getProviderUserId());
        assertEquals("iron-coder", command.getProviderLogin());
        assertEquals("Iron Coder", command.getDisplayName());
        assertEquals("https://avatars.githubusercontent.com/u/12345678", command.getAvatarUrl());
    }

    @Test
    @DisplayName("""
            Given a GitHub profile without a public name
            When it is read
            Then the display name is left empty, so registration falls back to the username
            """)
    void readLeavesDisplayNameEmptyWhenGitHubHasNoName() {
        // Given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", 12345678);
        attributes.put("login", "iron-coder");
        attributes.put("name", null);

        // When
        SignInCommand command = reader.read(gitHubUser(attributes));

        // Then
        assertNull(command.getDisplayName());
        assertNull(command.getAvatarUrl());
    }

    private static OAuth2User gitHubUser(Map<String, Object> attributes) {
        // "id" is the attribute Spring Security's GitHub setup uses as the user's name.
        return new DefaultOAuth2User(List.of(new SimpleGrantedAuthority("OAUTH2_USER")), attributes, "id");
    }
}
