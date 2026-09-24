package org.manu.hs.integration.learning;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.question.Vote;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * PUT and DELETE /learning/questions/{id}/vote
 */
class VoteOnQuestionTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a question learners can answer
            When a learner votes it up
            Then their vote is stored and HTTP 204 is returned
            """)
    void voteReturnsNoContentAndStoresVote() {
        // Given
        Long questionId = availableQuestion();
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = vote(questionId, """
                {
                    "vote": "UP"
                }
                """);

        // Then
        response.expectStatus().isNoContent();

        assertEquals("UP", findVote(questionId, OTHER_USER));
    }

    @Test
    @DisplayName("""
            Given a learner who voted a question up
            When they vote it down
            Then their vote is replaced, as each learner has one vote per question
            """)
    void voteReplacesEarlierVote() {
        // Given
        Long questionId = availableQuestion();
        insertVote(questionId, OTHER_USER, Vote.UP);
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = vote(questionId, """
                {
                    "vote": "DOWN"
                }
                """);

        // Then
        response.expectStatus().isNoContent();

        assertEquals("DOWN", findVote(questionId, OTHER_USER));
    }

    @Test
    @DisplayName("""
            Given a learner who voted a question up
            When they take their vote back
            Then their vote is removed and HTTP 204 is returned
            """)
    void clearVoteReturnsNoContentAndRemovesVote() {
        // Given
        Long questionId = availableQuestion();
        insertVote(questionId, OTHER_USER, Vote.UP);
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = restClient
                .delete()
                .uri("/learning/questions/{id}/vote", questionId)
                .exchange();

        // Then
        response.expectStatus().isNoContent();

        assertNull(findVote(questionId, OTHER_USER));
    }

    @Test
    @DisplayName("""
            Given a question learners can't answer, as it is benched
            When a learner tries to vote on it
            Then a not found response is returned and no vote is stored
            """)
    void voteReturnsNotFoundWhenQuestionIsNotAvailable() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.RETIRED, AUTHOR, "java-core");
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = vote(questionId, """
                {
                    "vote": "UP"
                }
                """);

        // Then
        response.expectStatus().isNotFound();

        assertNull(findVote(questionId, OTHER_USER));
    }

    @Test
    @DisplayName("""
            Given a question learners can answer
            When a learner sends a vote that is neither up nor down
            Then a bad request is returned and no vote is stored
            """)
    void voteReturnsBadRequestWhenVoteIsMissing() {
        // Given
        Long questionId = availableQuestion();
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = vote(questionId, "{}");

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("vote is required");

        assertNull(findVote(questionId, OTHER_USER));
    }

    @Test
    @DisplayName("""
            Given a question learners can answer
            When a learner sends the vote as a number (0) instead of its name
            Then a bad request is returned and no vote is stored: only the names UP and DOWN are
            accepted, so the order the values are declared in never becomes part of the API
            """)
    void voteReturnsBadRequestWhenVoteIsANumber() {
        // Given
        Long questionId = availableQuestion();
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = vote(questionId, """
                {
                    "vote": 0
                }
                """);

        // Then
        response.expectStatus().isBadRequest();

        assertNull(findVote(questionId, OTHER_USER));
    }

    private Long availableQuestion() {
        insertConcept("java-core");
        return insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
    }

    private RestTestClient.ResponseSpec vote(Long questionId, String requestBody) {
        return restClient
                .put()
                .uri("/learning/questions/{id}/vote", questionId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange();
    }
}
