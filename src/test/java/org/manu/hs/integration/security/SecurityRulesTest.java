package org.manu.hs.integration.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Rules that apply to every endpoint: who may call what, CSRF, CORS and signing out.
 * The rules themselves live in SecurityConfig.
 */
class SecurityRulesTest extends AbstractIntegrationTest {

    private static final String UI_ORIGIN = "http://localhost:5173";

    @Test
    @DisplayName("""
            Given a first-time visitor (or a bot) with no session
            When they ask for a CSRF token, call endpoints that require signing in, or ones that don't exist
            Then each is refused, and no sign-in session is created for them,
            so anonymous traffic can't fill the session table
            """)
    void refusedAnonymousRequestsCreateNoSession() {
        // Given
        int sessionsBefore = countSessions();

        // When / Then
        for (String uri : new String[]{"/csrf", "/users/me", "/content/questions/mine", "/admin/users", "/internal/anything"}) {
            RestTestClient.ResponseSpec response = clientWithoutSession().get().uri(uri).exchange();
            response
                    .expectStatus().value(status -> assertEquals(true, status == 401 || status == 403, uri))
                    .expectHeader().doesNotExist(HttpHeaders.SET_COOKIE);
        }

        assertEquals(sessionsBefore, countSessions());
    }

    @Test
    @DisplayName("""
            Given a visitor who is not signed in
            When they list the concepts and ask for a question to train on
            Then both are allowed, so visitors can try a workout before signing in
            """)
    void conceptsAndNextQuestionAreOpenToVisitors() {
        // Given
        actingAsVisitor();

        // When / Then
        restClient.get().uri("/content/concepts").exchange().expectStatus().isOk();
        restClient.get().uri("/learning/questions/next").exchange().expectStatus().isNoContent();
    }

    @Test
    @DisplayName("""
            Given a visitor who is not signed in
            When they read anything else under content or learning
            Then an unauthorized response is returned, as the rest requires signing in
            """)
    void otherContentAndLearningReadsReturnUnauthorizedWhenVisitor() {
        // Given
        actingAsVisitor();

        // When / Then
        for (String uri : new String[]{"/content/questions/mine", "/content/reviews", "/content/reports", "/learning/answers"}) {
            restClient.get().uri(uri).exchange()
                    .expectStatus().isUnauthorized()
                    .expectBody()
                    .jsonPath("$.message").isEqualTo("Sign in required");
        }
    }

    @Test
    @DisplayName("""
            Given a visitor who is not signed in
            When they submit an answer
            Then an unauthorized response is returned, as saving a set (and seeing the model answer)
            requires signing in
            """)
    void submittingAnAnswerReturnsUnauthorizedWhenVisitor() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAsVisitor();

        // When
        RestTestClient.ResponseSpec response = restClient
                .post()
                .uri("/learning/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        { "questionId": %d, "answer": "It prints 1" }
                        """.formatted(questionId))
                .exchange();

        // Then
        response.expectStatus().isUnauthorized();
        assertEquals(0, countAnswers());
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
            Given a visitor who is not signed in
            When they start signing in with GitHub
            Then they're sent to GitHub with prompt=select_account, so GitHub shows its account picker
            instead of silently using the account already signed in to github.com
            (and the picker leaves PKCE on: the redirect still carries the code challenge)
            """)
    void signingInAsksGitHubToShowTheAccountPicker() {
        // Given
        RestTestClient client = clientWithoutSession();

        // When
        RestTestClient.ResponseSpec response = client
                .get()
                .uri("/oauth2/authorization/github")
                .exchange();

        // Then
        response
                .expectStatus().is3xxRedirection()
                .expectHeader().value(HttpHeaders.LOCATION, location -> {
                    assertTrue(location.startsWith("https://github.com/login/oauth/authorize?"), location);
                    assertTrue(location.contains("prompt=select_account"), location);
                    assertTrue(location.contains("code_challenge="), location);
                    assertTrue(location.contains("code_challenge_method=S256"), location);
                });
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
            Then the API allows it, including the session cookie, and lets the browser reuse the answer for an hour
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
                .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true")
                .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
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
