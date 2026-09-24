package org.manu.hs.integration.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * POST /admin/invites
 */
class CreateInviteTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a signed-in admin
            When they invite a GitHub username
            Then the invite is stored as written, and returned with HTTP 201
            """)
    void createInviteStoresTheUsername() {
        // Given
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = invite("""
                { "login": "Octo-Cat" }
                """);

        // Then
        response
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.login").isEqualTo("Octo-Cat")
                .jsonPath("$.provider").isEqualTo("GITHUB");
        assertEquals(List.of("Octo-Cat"), findInvitedLogins());
    }

    @Test
    @DisplayName("""
            Given an invite for a username
            When an admin invites the same username in another case
            Then a conflict is returned and nothing is added
            """)
    void createInviteReturnsConflictForTheSameUsernameInAnyCase() {
        // Given
        insertInvite("octo-cat");
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = invite("""
                { "login": "Octo-Cat" }
                """);

        // Then
        response
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.message").isEqualTo("GitHub account 'Octo-Cat' is already invited");
        assertEquals(List.of("octo-cat"), findInvitedLogins());
    }

    @Test
    @DisplayName("""
            Given a signed-in admin
            When they invite something that can't be a GitHub username (blank, spaces, too long)
            Then a bad request is returned and nothing is added
            """)
    void createInviteReturnsBadRequestForSomethingThatIsNotAUsername() {
        // Given
        actingAs(ADMIN);

        // When / Then
        invite("{}").expectStatus().isBadRequest()
                .expectBody().jsonPath("$.message").isEqualTo("login is required");
        invite("""
                { "login": "two words" }
                """).expectStatus().isBadRequest()
                .expectBody().jsonPath("$.message").isEqualTo("login must be a GitHub username");
        invite("""
                { "login": "%s" }
                """.formatted("a".repeat(40))).expectStatus().isBadRequest();
        assertEquals(List.of(), findInvitedLogins());
    }

    @Test
    @DisplayName("""
            Given a signed-in member
            When they try to invite someone
            Then forbidden is returned and nothing is added
            """)
    void createInviteReturnsForbiddenToAMember() {
        // Given
        actingAs(AUTHOR);

        // When / Then
        invite("""
                { "login": "friend" }
                """).expectStatus().isForbidden();
        assertEquals(List.of(), findInvitedLogins());
    }

    private RestTestClient.ResponseSpec invite(String body) {
        return restClient.post()
                .uri("/admin/invites")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .exchange();
    }
}
