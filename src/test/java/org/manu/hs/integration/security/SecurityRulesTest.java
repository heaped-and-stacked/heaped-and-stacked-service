package org.manu.hs.integration.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * Rules that apply to every endpoint: who may call what, CSRF, CORS and signing out.
 * The rules themselves live in SecurityConfig.
 */
class SecurityRulesTest extends AbstractIntegrationTest {

    private static final String UI_ORIGIN = "http://localhost:5173";

    @Test
    @DisplayName("""
            Given a visitor who is not signed in
            When they read content
            Then an unauthorized response is returned, as content management requires signing in
            """)
    void contentReturnsUnauthorizedWhenVisitor() {
        // Given
        actingAsVisitor();

        // When
        RestTestClient.ResponseSpec response = restClient
                .get()
                .uri("/content/concepts")
                .exchange();

        // Then
        response
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Sign in required");
    }

    @Test
    @DisplayName("""
            Given a visitor who is not signed in
            When they ask for questions to train on
            Then an unauthorized response is returned, as learning requires signing in for now
            """)
    void learningReturnsUnauthorizedWhenVisitor() {
        // Given
        actingAsVisitor();

        // When
        RestTestClient.ResponseSpec response = restClient
                .get()
                .uri("/learning/questions/next")
                .exchange();

        // Then
        response.expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("""
            Given a visitor who is not signed in
            When they submit a question
            Then an unauthorized response is returned, as contributing requires signing in
            """)
    void submittingContentReturnsUnauthorizedWhenVisitor() {
        // Given
        actingAsVisitor();

        // When
        RestTestClient.ResponseSpec response = restClient
                .post()
                .uri("/content/questions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(validQuestionJson("java-core"))
                .exchange();

        // Then
        response.expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("""
            Given a signed-in member
            When they send a write request with a CSRF token that does not match their session
            Then a forbidden response is returned, as the request may come from another site
            """)
    void writeRequestReturnsForbiddenWhenCsrfTokenIsWrong() {
        // Given
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = restClient
                .put()
                .uri("/users/me")
                .header("X-CSRF-TOKEN", "not-the-session-token")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        { "displayName": "Forged" }
                        """)
                .exchange();

        // Then
        response.expectStatus().isForbidden();
    }

    @Test
    @DisplayName("""
            Given a signed-in member
            When they call a path the API does not declare
            Then a forbidden response is returned, as anything not explicitly allowed is denied
            """)
    void undeclaredPathReturnsForbidden() {
        // Given
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = restClient
                .get()
                .uri("/internal/anything")
                .exchange();

        // Then
        response.expectStatus().isForbidden();
    }

    @Test
    @DisplayName("""
            Given a visitor who is not signed in
            When they open /login
            Then an unauthorized response is returned, as signing in starts at the GitHub link, not a login page
            """)
    void builtInLoginPageIsNotServed() {
        // Given
        actingAsVisitor();

        // When
        RestTestClient.ResponseSpec response = restClient
                .get()
                .uri("/login")
                .exchange();

        // Then
        response.expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("""
            Given a visitor who is not signed in and API docs are enabled
            When they request the OpenAPI description
            Then it is returned, names the API and describes its endpoints,
            without asking clients for the CSRF token the server injects itself
            """)
    void apiDocsAreServedToVisitorsWhenEnabled() {
        // Given
        actingAsVisitor();

        // When
        RestTestClient.ResponseSpec response = restClient
                .get()
                .uri("/v3/api-docs")
                .exchange();

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.info.title").isEqualTo("Heaped & Stacked API")
                .jsonPath("$.paths['/content/concepts']").exists()
                .jsonPath("$.paths['/users/me']").exists()
                .jsonPath("$.paths['/csrf'].get.parameters").doesNotExist();
    }

    @Test
    @DisplayName("""
            Given the UI's origin
            When the browser checks whether it may call the API (CORS preflight)
            Then the API allows it, including the session cookie
            """)
    void corsPreflightIsAllowedForTheUi() {
        // When
        RestTestClient.ResponseSpec response = restClient
                .options()
                .uri("/users/me")
                .header(HttpHeaders.ORIGIN, UI_ORIGIN)
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "PUT")
                .exchange();

        // Then
        response
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, UI_ORIGIN)
                .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
    }

    @Test
    @DisplayName("""
            Given a website other than the UI
            When the browser checks whether it may call the API (CORS preflight)
            Then the API refuses it
            """)
    void corsPreflightIsRefusedForOtherOrigins() {
        // When
        RestTestClient.ResponseSpec response = restClient
                .options()
                .uri("/users/me")
                .header(HttpHeaders.ORIGIN, "https://evil.example.com")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "PUT")
                .exchange();

        // Then
        response.expectStatus().isForbidden();
    }

    @Test
    @DisplayName("""
            Given a signed-in member
            When they sign out
            Then HTTP 204 is returned and their next request is a visitor's
            """)
    void logoutEndsTheSession() {
        // Given
        actingAs(AUTHOR);

        // When
        restClient
                .post()
                .uri("/logout")
                .exchange()
                .expectStatus().isNoContent();

        // Then
        restClient
                .get()
                .uri("/users/me")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
