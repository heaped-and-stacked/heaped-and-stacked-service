package org.manu.hs.integration.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.integration.AbstractIntegrationTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DELETE /admin/invites/{id}
 */
class DeleteInviteTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given two invites
            When an admin removes one
            Then HTTP 204 is returned and only the other stays
            """)
    void deleteInviteRemovesIt() {
        // Given
        Long friend = insertInvite("friend");
        insertInvite("other");
        actingAs(ADMIN);

        // When / Then
        restClient.delete().uri("/admin/invites/{id}", friend).exchange().expectStatus().isNoContent();
        assertEquals(List.of("other"), findInvitedLogins());
    }

    @Test
    @DisplayName("""
            Given no invite with the given id
            When an admin tries to remove it
            Then not found is returned
            """)
    void deleteInviteReturnsNotFoundForAnUnknownId() {
        // Given
        actingAs(ADMIN);

        // When / Then
        restClient.delete().uri("/admin/invites/{id}", 999_999).exchange()
                .expectStatus().isNotFound()
                .expectBody().jsonPath("$.message").isEqualTo("Invite with id '999999' not found");
    }

    @Test
    @DisplayName("""
            Given an invite
            When a member tries to remove it
            Then forbidden is returned and the invite stays
            """)
    void deleteInviteReturnsForbiddenToAMember() {
        // Given
        Long friend = insertInvite("friend");
        actingAs(AUTHOR);

        // When / Then
        restClient.delete().uri("/admin/invites/{id}", friend).exchange().expectStatus().isForbidden();
        assertEquals(List.of("friend"), findInvitedLogins());
    }
}
