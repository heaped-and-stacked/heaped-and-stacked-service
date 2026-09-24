package org.manu.hs.integration.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Every response carries an X-Request-Id: the id under which the request's log lines are
 * written, so an error a user reports can be found in the logs. The filter lives in
 * RequestIdFilter.
 */
class RequestIdTest extends AbstractIntegrationTest {

    private static final String REQUEST_ID = "X-Request-Id";
    private static final String UI_ORIGIN = "http://localhost:5173";

    @Test
    @DisplayName("""
            Given requests that succeed, are refused, or hit nothing
            When each is answered
            Then every response carries its own short request id
            """)
    void everyResponseCarriesItsOwnRequestId() {
        // Given
        Set<String> ids = new HashSet<>();

        // When / Then
        for (String uri : new String[]{"/actuator/health", "/users/me", "/content/concepts", "/internal/anything"}) {
            String id = clientWithoutSession().get().uri(uri).exchange()
                    .returnResult()
                    .getResponseHeaders()
                    .getFirst(REQUEST_ID);
            assertNotNull(id, uri);
            assertTrue(id.matches("[0-9a-f]{12}"), uri + " -> " + id);
            ids.add(id);
        }
        assertEquals(4, ids.size());
    }

    @Test
    @DisplayName("""
            Given a request that already carries a request id, e.g. from a proxy in front of the API
            When it is answered
            Then the same id is used, so both sides' logs line up
            """)
    void incomingRequestIdIsKept() {
        // When
        RestTestClient.ResponseSpec response = clientWithoutSession().get().uri("/actuator/health")
                .header(REQUEST_ID, "proxy-7f3a-01")
                .exchange();

        // Then
        response.expectHeader().valueEquals(REQUEST_ID, "proxy-7f3a-01");
    }

    @Test
    @DisplayName("""
            Given a request whose request id is too long or has odd characters
            When it is answered
            Then a fresh id is used instead, so nobody can write junk into the logs
            """)
    void untrustworthyIncomingRequestIdIsReplaced() {
        for (String junk : new String[]{"x".repeat(65), "line\tbreak", "<script>", "a b"}) {
            // When
            String id = clientWithoutSession().get().uri("/actuator/health")
                    .header(REQUEST_ID, junk)
                    .exchange()
                    .returnResult()
                    .getResponseHeaders()
                    .getFirst(REQUEST_ID);

            // Then
            assertNotNull(id);
            assertTrue(id.matches("[0-9a-f]{12}"), junk + " -> " + id);
        }
    }

    @Test
    @DisplayName("""
            Given the web app, which runs on another origin
            When it calls the API
            Then it may read the request id, so it can show it in error messages
            """)
    void webAppMayReadTheRequestId() {
        // When
        RestTestClient.ResponseSpec response = clientWithoutSession().get().uri("/content/concepts")
                .header(HttpHeaders.ORIGIN, UI_ORIGIN)
                .exchange();

        // Then
        response.expectHeader().value(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
                exposed -> assertTrue(exposed.contains(REQUEST_ID), exposed));
    }
}
