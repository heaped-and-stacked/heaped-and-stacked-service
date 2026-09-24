package org.manu.hs.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for RateLimitFilter: per-client limits per minute, with a clock the test controls.
 */
class RateLimitFilterTest {

    private static final Instant START = Instant.parse("2026-10-10T10:00:00Z");

    private final AtomicReference<Instant> now = new AtomicReference<>(START);
    private final RateLimitFilter filter = new RateLimitFilter(true, new Clock() {
        @Override
        public ZoneOffset getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return now.get();
        }
    });

    @Test
    @DisplayName("""
            Given a client making writes
            When it makes more than 60 in one minute
            Then the 61st is refused with HTTP 429 and says when to retry
            """)
    void writesOverTheLimitAreRefusedWith429() throws Exception {
        // Given
        for (int i = 0; i < 60; i++) {
            assertEquals(200, send("POST", "/learning/answers", "10.0.0.1").getStatus());
        }

        // When
        now.set(START.plusSeconds(20));
        MockHttpServletResponse refused = send("POST", "/learning/answers", "10.0.0.1");

        // Then
        assertEquals(429, refused.getStatus());
        assertEquals("40", refused.getHeader("Retry-After"));
        assertTrue(refused.getContentAsString().contains("Too many requests"));
    }

    @Test
    @DisplayName("""
            Given a client that used up its writes for this minute
            When the next minute starts
            Then it may write again
            """)
    void aNewMinuteStartsAFreshCount() throws Exception {
        // Given
        for (int i = 0; i < 61; i++) {
            send("PUT", "/content/questions/1", "10.0.0.1");
        }

        // When
        now.set(START.plusSeconds(60));

        // Then
        assertEquals(200, send("PUT", "/content/questions/1", "10.0.0.1").getStatus());
    }

    @Test
    @DisplayName("""
            Given one client that used up its writes
            When another client writes
            Then the other client isn't limited: limits are per client
            """)
    void limitsArePerClient() throws Exception {
        // Given
        for (int i = 0; i < 61; i++) {
            send("DELETE", "/content/questions/1", "10.0.0.1");
        }

        // When / Then
        assertEquals(200, send("DELETE", "/content/questions/1", "10.0.0.2").getStatus());
    }

    @Test
    @DisplayName("""
            Given a client starting GitHub sign-ins
            When it starts more than 10 in one minute
            Then the 11th is refused, while its other requests still go through
            """)
    void signInHasItsOwnLowerLimit() throws Exception {
        // Given
        for (int i = 0; i < 10; i++) {
            assertEquals(200, send("GET", "/oauth2/authorization/github", "10.0.0.1").getStatus());
        }

        // When / Then
        assertEquals(429, send("GET", "/oauth2/authorization/github", "10.0.0.1").getStatus());
        assertEquals(200, send("GET", "/content/concepts", "10.0.0.1").getStatus());
    }

    @Test
    @DisplayName("""
            Given a client that started 10 GitHub sign-ins this minute, the most allowed
            When GitHub sends it back to finish signing in
            Then the callback goes through, as only starts count: refusing it would strand the person
            halfway, on a raw error, after they already approved on GitHub
            """)
    void gitHubCallbacksDontCountAsSignInStarts() throws Exception {
        // Given
        for (int i = 0; i < 10; i++) {
            assertEquals(200, send("GET", "/oauth2/authorization/github", "10.0.0.1").getStatus());
        }

        // When / Then
        assertEquals(200, send("GET", "/login/oauth2/code/github", "10.0.0.1").getStatus());
    }

    @Test
    @DisplayName("""
            Given a client reading pages
            When it makes 300 reads in one minute, then one more
            Then the 300 go through and the 301st is refused
            """)
    void readsHaveAGenerousLimit() throws Exception {
        // Given
        for (int i = 0; i < 300; i++) {
            assertEquals(200, send("GET", "/learning/questions/next", "10.0.0.1").getStatus());
        }

        // When / Then
        assertEquals(429, send("GET", "/learning/questions/next", "10.0.0.1").getStatus());
    }

    @Test
    @DisplayName("""
            Given an uptime monitor
            When it checks the health endpoint very often
            Then it is never limited
            """)
    void healthChecksAreNeverLimited() throws Exception {
        for (int i = 0; i < 400; i++) {
            assertEquals(200, send("GET", "/actuator/health", "10.0.0.1").getStatus());
        }
    }

    @Test
    @DisplayName("""
            Given rate limiting is switched off (as in the integration tests)
            When a client writes far over the limit
            Then nothing is refused
            """)
    void nothingIsLimitedWhenSwitchedOff() throws Exception {
        // Given
        RateLimitFilter off = new RateLimitFilter(false, Clock.systemUTC());

        // When / Then
        for (int i = 0; i < 100; i++) {
            MockHttpServletRequest request = new MockHttpServletRequest("POST", "/learning/answers");
            MockHttpServletResponse response = new MockHttpServletResponse();
            off.doFilter(request, response, (req, res) -> { });
            assertEquals(200, response.getStatus());
        }
    }

    private MockHttpServletResponse send(String method, String uri, String clientAddress) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(method, uri);
        request.setRemoteAddr(clientAddress);
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, (req, res) -> { });
        assertNotNull(response);
        return response;
    }
}
