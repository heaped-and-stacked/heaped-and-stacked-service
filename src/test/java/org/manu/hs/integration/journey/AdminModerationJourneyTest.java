package org.manu.hs.integration.journey;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.application.user.exception.UserNotActiveException;
import org.manu.hs.domain.user.AuthProvider;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Journeys: an admin moderates people. Promoting and blocking take effect right away,
 * because the affected user's sessions are ended.
 */
class AdminModerationJourneyTest extends AbstractIntegrationTest {

    /** The test profile makes this GitHub account a bootstrap admin. */
    private static final String BOOTSTRAP_ADMIN_GITHUB_ID = "999";

    @Test
    @DisplayName("""
            Given the platform owner (a bootstrap admin) and a member, both signed in
            When the owner promotes the member
            Then the member is signed out, and after signing in again can open the review queue
            """)
    void bootstrapAdminPromotesMemberWhoThenReviews() {
        // Given
        RestTestClient ownersBrowser = signInWithGitHub(BOOTSTRAP_ADMIN_GITHUB_ID, "the-owner");
        RestTestClient membersBrowser = signInWithGitHub("5001", "future-admin");
        Long memberId = findIdentity(AuthProvider.GITHUB, "5001").getUserId();

        membersBrowser.get().uri("/content/questions/review").exchange()
                .expectStatus().isForbidden();

        // When: the owner promotes the member
        ownersBrowser.put().uri("/admin/users/{id}/role", memberId)
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        { "role": "ADMIN" }
                        """)
                .exchange()
                .expectStatus().isOk();

        // Then: the member's old session is gone...
        membersBrowser.get().uri("/users/me").exchange()
                .expectStatus().isUnauthorized();

        // ...and once they sign in again, they have admin rights
        RestTestClient membersNewBrowser = signInWithGitHub("5001", "future-admin");
        membersNewBrowser.get().uri("/content/questions/review").exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("""
            Given a signed-in member
            When an admin blocks them, and later unblocks them
            Then while blocked they are signed out and can't sign in; once unblocked they can sign in again
            """)
    void adminBlocksThenUnblocksMember() {
        // Given
        RestTestClient membersBrowser = signInWithGitHub("5001", "troublemaker");
        Long memberId = findIdentity(AuthProvider.GITHUB, "5001").getUserId();
        actingAs(ADMIN);

        // When: the admin blocks them
        changeStatus(memberId, "BLOCKED");

        // Then: they are signed out and can't sign in again
        membersBrowser.get().uri("/content/concepts").exchange()
                .expectStatus().isUnauthorized();
        assertThrows(UserNotActiveException.class, () -> signInWithGitHub("5001", "troublemaker"));

        // When: the admin unblocks them
        changeStatus(memberId, "ACTIVE");

        // Then: they can sign in and use the platform again
        signInWithGitHub("5001", "troublemaker")
                .get().uri("/content/concepts").exchange()
                .expectStatus().isOk();
    }

    private void changeStatus(Long userId, String status) {
        restClient.put().uri("/admin/users/{id}/status", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        { "status": "%s" }
                        """.formatted(status))
                .exchange()
                .expectStatus().isOk();
    }
}
