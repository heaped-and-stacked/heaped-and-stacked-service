package org.manu.hs.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.application.user.UserApplication;
import org.manu.hs.application.user.exception.SignInNotInvitedException;
import org.manu.hs.application.user.exception.UserNotActiveException;
import org.manu.hs.domain.user.UserStatus;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for OAuth2SignInService: what happens after GitHub confirmed who signed in. Every
 * failure must be an OAuth2AuthenticationException, the only kind Spring Security turns into the
 * redirect back to the web app (?signInError); anything else ends on a raw error page.
 */
class OAuth2SignInServiceTest {

    private final UserApplication userApplication = mock(UserApplication.class);
    private final OAuth2SignInService signInService = new OAuth2SignInService(
            request -> new DefaultOAuth2User(
                    List.of(new SimpleGrantedAuthority("OAUTH2_USER")),
                    Map.of("id", 12345678, "login", "iron-coder"),
                    "id"),
            userApplication,
            List.of(new GitHubProfileReader()));

    @Test
    @DisplayName("""
            Given a blocked account
            When its owner signs in with GitHub
            Then the sign-in fails as "account_not_active", so the web app can say why
            """)
    void blockedAccountFailsAsNotActive() {
        // Given
        when(userApplication.signIn(any())).thenThrow(new UserNotActiveException(1004L, UserStatus.BLOCKED));

        // When
        OAuth2AuthenticationException exception = assertThrows(
                OAuth2AuthenticationException.class, () -> signInService.loadUser(gitHubRequest()));

        // Then
        assertEquals("account_not_active", exception.getError().getErrorCode());
    }

    @Test
    @DisplayName("""
            Given sign-in is invite-only and the account isn't invited
            When it signs in with GitHub
            Then the sign-in fails as "not_invited", so the web app can say why
            """)
    void notInvitedAccountFailsAsNotInvited() {
        // Given
        when(userApplication.signIn(any())).thenThrow(new SignInNotInvitedException("stranger"));

        // When
        OAuth2AuthenticationException exception = assertThrows(
                OAuth2AuthenticationException.class, () -> signInService.loadUser(gitHubRequest()));

        // Then
        assertEquals("not_invited", exception.getError().getErrorCode());
    }

    @Test
    @DisplayName("""
            Given GitHub confirmed who signed in, but saving the sign-in fails (the database is down)
            When the sign-in completes
            Then it fails as "sign_in_failed", so the browser goes back to the web app with
            ?signInError instead of a raw error page on the API
            """)
    void unexpectedFailureStillReturnsToTheWebApp() {
        // Given
        when(userApplication.signIn(any())).thenThrow(new DataAccessResourceFailureException("database down"));

        // When
        OAuth2AuthenticationException exception = assertThrows(
                OAuth2AuthenticationException.class, () -> signInService.loadUser(gitHubRequest()));

        // Then
        assertEquals("sign_in_failed", exception.getError().getErrorCode());
    }

    private static OAuth2UserRequest gitHubRequest() {
        ClientRegistration gitHub = ClientRegistration.withRegistrationId("github")
                .clientId("test-client-id")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .authorizationUri("https://github.com/login/oauth/authorize")
                .tokenUri("https://github.com/login/oauth/access_token")
                .userInfoUri("https://api.github.com/user")
                .userNameAttributeName("id")
                .build();
        OAuth2AccessToken token = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER, "token", Instant.now(), Instant.now().plusSeconds(60));
        return new OAuth2UserRequest(gitHub, token);
    }
}
