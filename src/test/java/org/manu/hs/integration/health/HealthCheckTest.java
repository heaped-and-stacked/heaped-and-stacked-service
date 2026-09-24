package org.manu.hs.integration.health;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * GET /actuator/health (and its liveness and readiness probes)
 */
class HealthCheckTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given the API is running and its database is reachable
            When an uptime monitor, which has no session, sends a GET request to the health endpoint
            Then UP is returned with HTTP 200, and no sign-in session is created for it
            """)
    void healthReturnsUpWithoutCreatingASession() {
        // Given
        int sessionsBefore = countSessions();

        // When
        RestTestClient.ResponseSpec response = clientWithoutSession()
                .get()
                .uri("/actuator/health")
                .exchange();

        // Then
        response
                .expectStatus().isOk()
                .expectHeader().doesNotExist("Set-Cookie")
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP")
                .jsonPath("$.components").doesNotExist();

        assertEquals(sessionsBefore, countSessions());
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"/actuator/health/liveness", "/actuator/health/readiness"})
    @DisplayName("""
            Given the API is running
            When the platform probes whether it is alive (restart it if not) or ready (send it traffic)
            Then UP is returned with HTTP 200, without a session: liveness never depends on the
            database, so a database hiccup can't make the platform restart a healthy API
            """)
    void livenessAndReadinessProbesAnswerUp(String uri) {
        // When
        RestTestClient.ResponseSpec response = clientWithoutSession()
                .get()
                .uri(uri)
                .exchange();

        // Then
        response
                .expectStatus().isOk()
                .expectHeader().doesNotExist("Set-Cookie")
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");
    }

    @Test
    @DisplayName("""
            Given the API is running
            When anyone asks for another actuator endpoint, such as the environment or metrics
            Then it isn't served: only health is exposed
            """)
    void otherActuatorEndpointsAreNotExposed() {
        // When / Then
        for (String uri : new String[]{"/actuator", "/actuator/env", "/actuator/metrics", "/actuator/beans"}) {
            clientWithoutSession().get().uri(uri).exchange()
                    .expectStatus().value(status -> {
                        if (status == 200) {
                            throw new AssertionError(uri + " must not be served");
                        }
                    });
        }
    }
}
