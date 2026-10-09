package org.manu.hs.integration.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.question.Vote;
import org.manu.hs.domain.user.AuthProvider;
import org.manu.hs.domain.user.User;
import org.manu.hs.domain.user.UserStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * DELETE /admin/users/{id}: the head coach deletes an account, e.g. on an emailed request.
 */
class DeleteUserTest extends AbstractIntegrationTest {

    private static final Long HOUSE = 1005L;

    @Test
    @DisplayName("""
            Given a member with a GitHub login, a set, a vote and feedback
            When the admin sends a DELETE request for their account
            Then it's soft deleted as if they had done it themselves, and their activity stays
            """)
    void deleteUserSoftDeletesTheAccountAndKeepsTheActivity() {
        // Given
        insertIdentity(AUTHOR, AuthProvider.GITHUB, "5001", "author-on-github");
        Long questionId = givenActivityOf(AUTHOR);
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = deleteUser(AUTHOR, "");

        // Then
        response.expectStatus().isNoContent();

        User deleted = findUser(AUTHOR);
        assertEquals(UserStatus.DELETED, deleted.getStatus());
        assertNull(deleted.getDisplayName());
        assertNull(deleted.getAvatarUrl());
        assertEquals(0, countIdentitiesOf(AUTHOR));
        assertEquals(3, countActivityOf(AUTHOR));
        assertEquals(1, countQuestions(), "question " + questionId + " stays");
    }

    @Test
    @DisplayName("""
            Given a member with a set, a vote and feedback, and another member with their own
            When the admin sends a DELETE request for the first member's account, erasing their activity
            Then the first member's sets, votes and feedback are gone, and the other member's stay
            """)
    void deleteUserErasesTheActivityWhenAsked() {
        // Given
        givenActivityOf(AUTHOR);
        insertVote(insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core"), OTHER_USER, Vote.DOWN);
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = deleteUser(AUTHOR, "?eraseActivity=true");

        // Then
        response.expectStatus().isNoContent();
        assertEquals(UserStatus.DELETED, findUser(AUTHOR).getStatus());
        assertEquals(0, countActivityOf(AUTHOR));
        assertEquals(1, countActivityOf(OTHER_USER));
    }

    @Test
    @DisplayName("""
            Given the house account, which owns the gym's content
            When the admin sends a DELETE request for it
            Then a conflict is returned, and it stays as it is
            """)
    void deleteUserRefusesTheHouseAccount() {
        // Given
        insertHouseAccount(HOUSE);
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = deleteUser(HOUSE, "");

        // Then
        response.expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.message").isEqualTo(
                        "User '1005' is the house account, which owns the gym's content: it can't be deleted, blocked or have its role changed");
        assertEquals(UserStatus.ACTIVE, findUser(HOUSE).getStatus());
    }

    @Test
    @DisplayName("""
            Given the admin is the only active admin
            When they send a DELETE request for their own account
            Then a conflict is returned: the gym would have nobody to run it
            """)
    void deleteUserRefusesTheLastActiveAdmin() {
        // Given
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = deleteUser(ADMIN, "");

        // Then
        response.expectStatus().isEqualTo(409);
        assertEquals(UserStatus.ACTIVE, findUser(ADMIN).getStatus());
    }

    @Test
    @DisplayName("""
            Given a signed-in member who isn't an admin
            When they send a DELETE request for another member's account
            Then forbidden is returned
            """)
    void deleteUserReturnsForbiddenForMembers() {
        // Given
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = deleteUser(OTHER_USER, "");

        // Then
        response.expectStatus().isForbidden();
        assertEquals(UserStatus.ACTIVE, findUser(OTHER_USER).getStatus());
    }

    /** A set, a vote and feedback by the user, on one question; returns its id. */
    private Long givenActivityOf(Long userId) {
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, OTHER_USER, "java-core");
        actingAs(userId);
        submitAnswer(questionId, "An answer");
        insertVote(questionId, userId, Vote.UP);
        insertReport(questionId, userId);
        return questionId;
    }

    private RestTestClient.ResponseSpec deleteUser(Long userId, String query) {
        return restClient
                .delete()
                .uri("/admin/users/" + userId + query)
                .exchange();
    }
}
