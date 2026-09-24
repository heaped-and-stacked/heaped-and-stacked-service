package org.manu.hs.integration.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * GET /admin/invites
 */
class GetInvitesTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given two invites
            When an admin sends a GET request to the invites endpoint
            Then both are returned, by username in any case, each with its provider and when it was added
            """)
    void getInvitesReturnsEveryInviteByUsername() {
        // Given
        Long zed = insertInvite("zed");
        Long anna = insertInvite("Anna");
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = getInvites();

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].id").isEqualTo(anna.intValue())
                .jsonPath("$[0].login").isEqualTo("Anna")
                .jsonPath("$[0].provider").isEqualTo("GITHUB")
                .jsonPath("$[0].invitedAt").exists()
                .jsonPath("$[1].id").isEqualTo(zed.intValue());
    }

    @Test
    @DisplayName("""
            Given no invites: sign-in is open to everyone
            When an admin sends a GET request to the invites endpoint
            Then an empty list is returned
            """)
    void getInvitesReturnsAnEmptyListWhenSignInIsOpen() {
        // Given
        actingAs(ADMIN);

        // When / Then
        getInvites()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    @DisplayName("""
            Given a signed-in member
            When they send a GET request to the invites endpoint
            Then forbidden is returned: only admins manage who may sign in
            """)
    void getInvitesReturnsForbiddenToAMember() {
        // Given
        insertInvite("friend");
        actingAs(AUTHOR);

        // When / Then
        getInvites().expectStatus().isForbidden();
    }

    private RestTestClient.ResponseSpec getInvites() {
        return restClient.get().uri("/admin/invites").exchange();
    }
}
