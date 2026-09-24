package org.manu.hs.integration.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.manu.hs.api.user.dto.UserResponse;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * PUT /users/me
 */
class UpdateCurrentUserTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a signed-in member
            When a PUT request is sent to the current user endpoint with a new display name
            Then the display name is changed and the profile is returned with HTTP 200
            """)
    void updateCurrentUserReturnsOkWhenDisplayNameIsValid() {
        // Given
        actingAs(AUTHOR);

        String requestBody = """
                {
                    "displayName": "Iron Coder"
                }
                """;

        // When
        RestTestClient.ResponseSpec response = updateCurrentUser(requestBody);

        // Then
        UserResponse user = response
                .expectStatus().isOk()
                .expectBody(UserResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(user);
        assertEquals("Iron Coder", user.getDisplayName());

        // Verify database entry
        assertEquals("Iron Coder", findUser(AUTHOR).getDisplayName());
    }

    static Stream<Arguments> invalidDisplayNames() {
        return Stream.of(
                arguments("display name is missing", "{}", "displayName is required"),
                arguments("display name is blank", """
                        { "displayName": "   " }
                        """, "displayName is required"),
                arguments("display name is longer than 200 characters", """
                        { "displayName": "%s" }
                        """.formatted("x".repeat(201)), "displayName must be at most 200 characters"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidDisplayNames")
    @DisplayName("""
            Given a signed-in member and an invalid display name
            When a PUT request is sent to the current user endpoint
            Then a bad request naming the problem is returned and the profile is unchanged
            """)
    void updateCurrentUserReturnsBadRequestWhenDisplayNameIsInvalid(String description, String requestBody, String expectedMessage) {
        // Given
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = updateCurrentUser(requestBody);

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo(expectedMessage);

        assertEquals("User " + AUTHOR, findUser(AUTHOR).getDisplayName());
    }

    @Test
    @DisplayName("""
            Given a visitor who is not signed in
            When a PUT request is sent to the current user endpoint
            Then an unauthorized response is returned
            """)
    void updateCurrentUserReturnsUnauthorizedWhenVisitor() {
        // Given
        actingAsVisitor();

        // When
        RestTestClient.ResponseSpec response = updateCurrentUser("""
                { "displayName": "Nobody" }
                """);

        // Then
        response.expectStatus().isUnauthorized();
    }

    private RestTestClient.ResponseSpec updateCurrentUser(String requestBody) {
        return restClient
                .put()
                .uri("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange();
    }
}
