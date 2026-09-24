package org.manu.hs.integration.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * What happens when something fails that nobody planned for: the error is logged once, with its
 * stack trace and request id, and the client gets a plain message plus the request id to quote.
 * The handler lives in GlobalExceptionHandler.
 */
@ExtendWith(OutputCaptureExtension.class)
@Import(UnexpectedErrorTest.FailingController.class)
class UnexpectedErrorTest extends AbstractIntegrationTest {

    /**
     * Exists only in this test (imported above): fails the way a bug would. Under /content, so a
     * signed-in user may call it.
     */
    @RestController
    static class FailingController {
        @GetMapping("/content/test-only/unexpected-failure")
        public String fail() {
            throw new IllegalStateException("the database said no (test)");
        }

        @GetMapping("/content/test-only/lost-race")
        public String loseARace() {
            throw new DuplicateKeyException("duplicate key value violates unique constraint (test)");
        }

        @GetMapping("/content/test-only/stale-write")
        public String writeAStaleCopy() {
            throw new OptimisticLockingFailureException("changed meanwhile (test)");
        }
    }

    @Test
    @DisplayName("""
            Given an endpoint that fails unexpectedly
            When a signed-in member calls it
            Then HTTP 500 is returned with a plain message and the request id, nothing internal,
            and the error is logged with its stack trace, tagged with the same request id
            """)
    @SuppressWarnings("unchecked")
    void unexpectedErrorIsLoggedWithRequestIdAndHiddenFromTheClient(CapturedOutput output) {
        // When
        EntityExchangeResult<Map> result = restClient.get().uri("/content/test-only/unexpected-failure")
                .exchange()
                .expectStatus().isEqualTo(500)
                .expectBody(Map.class)
                .returnResult();

        // Then
        String requestId = result.getResponseHeaders().getFirst("X-Request-Id");
        Map<String, Object> body = result.getResponseBody();
        assertNotNull(requestId);
        assertNotNull(body);
        assertEquals("Something went wrong", body.get("message"));
        assertEquals(requestId, body.get("requestId"));
        assertFalse(body.toString().contains("database said no"), "nothing internal reaches the client");

        String logged = output.getAll();
        assertTrue(logged.contains("[" + requestId + "]"), "the log line carries the request id");
        assertTrue(logged.contains("java.lang.IllegalStateException: the database said no (test)"), "with the stack trace");
        assertEquals(1, logged.split("the database said no \\(test\\)", -1).length - 1, "logged once");
    }

    @Test
    @DisplayName("""
            Given an endpoint that exists
            When it is called with a method it doesn't support
            Then HTTP 405 is returned, not 500: Spring's own request errors keep their status
            and their headers (Allow lists the methods that are supported)
            """)
    void unsupportedMethodStaysA405(CapturedOutput output) {
        // When / Then
        restClient.patch().uri("/content/concepts")
                .exchange()
                .expectStatus().isEqualTo(405)
                .expectHeader().exists("Allow");

        assertFalse(output.getAll().contains(" ERROR "), "not logged as an error");
    }
    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"/content/test-only/lost-race", "/content/test-only/stale-write"})
    @DisplayName("""
            Given a write that lost a race: a unique index refused a duplicate a moment after the
            check passed, or the row changed after it was read
            When it reaches the client
            Then HTTP 409 is returned, asking to reload and try again: a conflict, not a server error
            """)
    void lostRacesAreConflicts(String uri, CapturedOutput output) {
        // When / Then
        restClient.get().uri(uri)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Someone else changed this just now: reload and try again");

        assertFalse(output.getAll().contains(" ERROR "), "not logged as an error");
    }
}
