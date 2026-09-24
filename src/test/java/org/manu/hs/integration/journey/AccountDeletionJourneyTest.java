package org.manu.hs.integration.journey;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.api.content.dto.QuestionResponse;
import org.manu.hs.domain.user.AuthProvider;
import org.manu.hs.domain.user.UserStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Journey: a member contributes, deletes their account, and later signs in again.
 */
class AccountDeletionJourneyTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a member who has submitted a question
            When they delete their account and later sign in with the same GitHub account
            Then their old question stays with the anonymised old account, and they start over as a new member
            """)
    void memberDeletesAccountThenStartsOver() {
        // Given
        insertConcept("java-core");
        RestTestClient browser = signInWithGitHub("5001", "leaving-coder");
        Long oldUserId = findIdentity(AuthProvider.GITHUB, "5001").getUserId();

        QuestionResponse question = browser.post().uri("/content/questions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(validQuestionJson("java-core"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(QuestionResponse.class)
                .returnResult()
                .getResponseBody();
        assertNotNull(question);

        // When: they delete their account
        browser.delete().uri("/users/me").exchange()
                .expectStatus().isNoContent();

        // Then: they are signed out, and the old account is anonymised
        browser.get().uri("/users/me").exchange()
                .expectStatus().isUnauthorized();
        assertEquals(UserStatus.DELETED, findUser(oldUserId).getStatus());
        assertNull(findUser(oldUserId).getDisplayName());

        // And: the question they wrote is kept, still pointing at the old account
        assertEquals(oldUserId, findQuestionById(question.getId()).getCreatedBy());

        // When: they sign in again with the same GitHub account
        RestTestClient newBrowser = signInWithGitHub("5001", "leaving-coder");
        Long newUserId = findIdentity(AuthProvider.GITHUB, "5001").getUserId();

        // Then: they are a new member, who does not own the old question
        assertNotEquals(oldUserId, newUserId);
        newBrowser.get().uri("/content/questions").exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.items").isEmpty()
                .jsonPath("$.totalItems").isEqualTo(0);
    }
}
