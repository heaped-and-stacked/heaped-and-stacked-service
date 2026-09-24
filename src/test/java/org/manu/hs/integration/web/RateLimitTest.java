package org.manu.hs.integration.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Rate limiting on the running API, switched on here (the other integration tests run without),
 * behind a proxy as in production. The exact counts are covered by RateLimitFilterTest; these
 * tests send until refused, so a minute ending halfway through doesn't make them fail.
 */
@TestPropertySource(properties = {"app.rate-limit.enabled=true", "server.forward-headers-strategy=native"})
class RateLimitTest extends AbstractIntegrationTest {

    /** 10 sign-in starts a minute: even across a minute boundary, the 21st start is refused. */
    private static final int ENOUGH_TO_BE_REFUSED = 21;

    private static final String UI_ORIGIN = "http://localhost:5173";

    @Test
    @DisplayName("""
            Given a client that keeps starting GitHub sign-ins
            When it goes past 10 in a minute
            Then it is refused with HTTP 429, a Retry-After and a request id, before GitHub is involved
            """)
    void signInStartsPastTheLimitAreRefused() {
        // Given / When
        RestTestClient.ResponseSpec refused = startSignInsUntilRefused(null);

        // Then
        refused
                .expectHeader().exists("Retry-After")
                .expectHeader().exists("X-Request-Id")
                .expectBody()
                .jsonPath("$.message").isEqualTo("Too many requests: slow down a little and try again");
    }

    @Test
    @DisplayName("""
            Given a client behind the proxy that sends a made-up X-Forwarded-For entry with every request
            When it keeps starting GitHub sign-ins
            Then it is still refused, as the limit counts the address the proxy added, not the ones
            the client wrote
            """)
    void madeUpForwardedAddressesDontEscapeTheLimit() {
        // Given / When / Then
        startSignInsUntilRefused("203.0.113.7");
    }

    @Test
    @DisplayName("""
            Given the web app, calling from its own origin
            When it goes past 60 writes in a minute
            Then the 429 carries the CORS headers, so the browser lets the app read it and ask the
            person to slow down, instead of reporting the API as unreachable
            """)
    void refusalsToTheUiAreReadableByTheBrowser() {
        // Given / When
        for (int i = 1; i <= 121; i++) {
            RestTestClient.ResponseSpec response = clientWithoutSession()
                    .post()
                    .uri("/logout")
                    .header(HttpHeaders.ORIGIN, UI_ORIGIN)
                    .header("X-Forwarded-For", "192.0.2.44")
                    .exchange();
            int[] status = new int[1];
            response.expectStatus().value(value -> status[0] = value);

            // Then
            if (status[0] == 429) {
                response
                        .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, UI_ORIGIN)
                        .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
                return;
            }
        }
        throw new AssertionError("never refused after 121 writes");
    }

    /** proxiedClient: the address the proxy appends after the client's made-up entry; null: no proxy. */
    private RestTestClient.ResponseSpec startSignInsUntilRefused(String proxiedClient) {
        for (int i = 1; i <= ENOUGH_TO_BE_REFUSED; i++) {
            RestTestClient.RequestHeadersSpec<?> request = clientWithoutSession().get().uri("/oauth2/authorization/github");
            if (proxiedClient != null) {
                request = request.header("X-Forwarded-For", "198.51.100." + i + ", " + proxiedClient);
            }
            RestTestClient.ResponseSpec response = request.exchange();
            int[] status = new int[1];
            response.expectStatus().value(value -> status[0] = value);
            if (status[0] == 429) {
                return response;
            }
            assertTrue(status[0] >= 300 && status[0] < 400, "start " + i + " got " + status[0]);
        }
        throw new AssertionError("never refused after " + ENOUGH_TO_BE_REFUSED + " sign-in starts");
    }
}
