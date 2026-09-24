package org.manu.hs.integration.learning;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * PUT /learning/answers/{id}/assessment
 */
class AssessAnswerTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a learner's own answer
            When they grade their answer and say how hard the question felt
            Then both are stored and the answer is returned with HTTP 200
            """)
    void assessAnswerReturnsOkWhenLearnerAssessesOwnAnswer() {
        // Given
        Long answerId = answerAsOtherUser();

        // When
        RestTestClient.ResponseSpec response = assess(answerId, """
                {
                    "selfGrade": 9,
                    "perceivedDifficulty": 7
                }
                """);

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.selfGrade").isEqualTo(9)
                .jsonPath("$.perceivedDifficulty").isEqualTo(7);

        Map<String, Object> row = findAnswerRow(answerId);
        assertEquals(9, ((Number) row.get("self_grade")).intValue());
        assertEquals(7, ((Number) row.get("perceived_difficulty")).intValue());
    }

    @Test
    @DisplayName("""
            Given a learner's own answer, already graded and rated for difficulty
            When they send only a new grade
            Then the assessment is replaced as a whole: the new grade is stored and the difficulty
            left out is cleared, which is how the app takes a value back (both parts are optional)
            """)
    void assessAnswerReplacesTheWholeAssessment() {
        // Given
        Long answerId = answerAsOtherUser();
        assess(answerId, """
                {
                    "selfGrade": 8,
                    "perceivedDifficulty": 6
                }
                """).expectStatus().isOk();

        // When
        RestTestClient.ResponseSpec response = assess(answerId, """
                {
                    "selfGrade": 3
                }
                """);

        // Then
        response.expectStatus().isOk();

        Map<String, Object> row = findAnswerRow(answerId);
        assertEquals(3, ((Number) row.get("self_grade")).intValue());
        assertNull(row.get("perceived_difficulty"));
    }

    @Test
    @DisplayName("""
            Given someone else's answer
            When a learner tries to assess it
            Then a forbidden response is returned and the answer stays unassessed
            """)
    void assessAnswerReturnsForbiddenWhenAnswerIsSomeoneElses() {
        // Given
        Long answerId = answerAsOtherUser();
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = assess(answerId, """
                {
                    "selfGrade": 10
                }
                """);

        // Then
        response.expectStatus().isForbidden();

        assertNull(findAnswerRow(answerId).get("self_grade"));
    }

    @Test
    @DisplayName("""
            Given no answer exists for the given id
            When a learner sends a self-assessment for it
            Then a not found response is returned
            """)
    void assessAnswerReturnsNotFoundWhenAnswerDoesNotExist() {
        // Given
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = assess(999_999L, """
                {
                    "selfGrade": 5
                }
                """);

        // Then
        response
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Answer with id '999999' not found");
    }

    @Test
    @DisplayName("""
            Given a learner's own answer
            When they send a grade outside 1 to 10
            Then a bad request is returned and nothing is stored
            """)
    void assessAnswerReturnsBadRequestWhenGradeIsOutOfRange() {
        // Given
        Long answerId = answerAsOtherUser();

        // When
        RestTestClient.ResponseSpec response = assess(answerId, """
                {
                    "selfGrade": 11
                }
                """);

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("selfGrade must be between 1 and 10");

        assertNull(findAnswerRow(answerId).get("self_grade"));
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"5.9", "\"7\""})
    @DisplayName("""
            Given a learner's own answer
            When they send a grade that isn't a whole JSON number (a decimal, or a number in quotes)
            Then a bad request is returned and nothing is stored, rather than the value being quietly
            cut down or converted
            """)
    void assessAnswerReturnsBadRequestWhenGradeIsNotAWholeNumber(String grade) {
        // Given
        Long answerId = answerAsOtherUser();

        // When
        RestTestClient.ResponseSpec response = assess(answerId, """
                {
                    "selfGrade": %s
                }
                """.formatted(grade));

        // Then
        response.expectStatus().isBadRequest();

        assertNull(findAnswerRow(answerId).get("self_grade"));
    }

    /** An answer given by OTHER_USER, who stays signed in. */
    private Long answerAsOtherUser() {
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(OTHER_USER);
        return submitAnswer(questionId, "Because of type erasure");
    }

    private RestTestClient.ResponseSpec assess(Long answerId, String requestBody) {
        return restClient
                .put()
                .uri("/learning/answers/{id}/assessment", answerId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange();
    }
}
