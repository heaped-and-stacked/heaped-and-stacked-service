package org.manu.hs.integration.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Request bodies have a size limit, checked before they're read. The filter lives in
 * RequestSizeLimitFilter.
 */
class RequestSizeLimitTest extends AbstractIntegrationTest {

    private static final String UI_ORIGIN = "http://localhost:5173";

    @Test
    @DisplayName("""
            Given a signed-in member
            When they send a request body far over the size limit
            Then it is refused with HTTP 413 and a request id, and nothing is stored
            """)
    void oversizedBodyIsRefusedWith413() {
        // Given
        insertConcept("java-core");
        String hugeText = "x".repeat(300 * 1024);

        // When
        RestTestClient.ResponseSpec response = submitQuestion(hugeText, "A model answer");

        // Then
        response
                .expectStatus().isEqualTo(413)
                .expectHeader().exists("X-Request-Id")
                .expectBody()
                .jsonPath("$.message").isEqualTo("Request body too large");

        assertEquals(0, countQuestions());
    }

    @Test
    @DisplayName("""
            Given the web app, calling from its own origin
            When it sends a request body far over the size limit
            Then the 413 carries the CORS headers, so the browser lets the app read it and say what
            went wrong, instead of reporting the API as unreachable
            """)
    void oversizedBodyFromTheUiIsReadableByTheBrowser() {
        // Given
        String hugeText = "x".repeat(300 * 1024);

        // When
        RestTestClient.ResponseSpec response = restClient
                .post()
                .uri("/content/questions")
                .header(HttpHeaders.ORIGIN, UI_ORIGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        { "requirement": "%s" }
                        """.formatted(hugeText))
                .exchange();

        // Then
        response
                .expectStatus().isEqualTo(413)
                .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, UI_ORIGIN)
                .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
    }

    @Test
    @DisplayName("""
            Given a signed-in member
            When they submit the largest exercise the rules allow, in characters that take several bytes
            Then it is accepted: the size limit leaves room for every valid request
            """)
    void largestValidExerciseIsAccepted() {
        // Given
        insertConcept("java-core");
        String longestText = "€".repeat(10_000); // 10,000 characters of 3 bytes each: 30 KB

        // When
        RestTestClient.ResponseSpec response = submitQuestion(longestText, longestText);

        // Then
        response.expectStatus().isCreated();

        assertEquals(1, countQuestions());
    }

    private RestTestClient.ResponseSpec submitQuestion(String requirement, String sampleSolution) {
        return restClient
                .post()
                .uri("/content/questions")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "title": "%s",
                            "requirement": "%s",
                            "sampleSolution": "%s",
                            "difficultyScore": 3,
                            "source": "%s",
                            "conceptTags": ["java-core"]
                        }
                        """.formatted("t".repeat(120), requirement, sampleSolution, "s".repeat(200)))
                .exchange();
    }
}
