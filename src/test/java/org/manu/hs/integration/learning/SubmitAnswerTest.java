package org.manu.hs.integration.learning;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.api.learning.dto.AnswerResponse;
import org.manu.hs.domain.question.ConceptLinkStatus;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * POST /learning/answers
 */
class SubmitAnswerTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a question available for learning
            When a learner sends a POST request with their answer
            Then the answer is stored with a copy of the question as it was, and returned with HTTP 201
            together with the sample solution, without a self-assessment yet
            """)
    void submitAnswerReturnsCreatedWithSampleSolution() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = submit("""
                {
                    "questionId": %d,
                    "answer": "Because of type erasure"
                }
                """.formatted(questionId));

        // Then
        AnswerResponse answer = response
                .expectStatus().isCreated()
                .expectBody(AnswerResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(answer);
        assertNotNull(answer.getId());
        assertEquals(questionId, answer.getQuestionId());
        assertEquals("Because of type erasure", answer.getAnswer());
        assertEquals("Original title", answer.getQuestion().getTitle());
        assertEquals("Original requirement", answer.getQuestion().getRequirement());
        assertEquals("Original solution", answer.getQuestion().getSampleSolution());
        assertEquals(List.of("java-core"), answer.getQuestion().getConceptTags());
        assertNotNull(answer.getAnsweredAt());
        assertNull(answer.getSelfGrade());
        assertNull(answer.getPerceivedDifficulty());

        // Verify database entry
        Map<String, Object> row = findAnswerRow(answer.getId());
        assertEquals(OTHER_USER, ((Number) row.get("user_id")).longValue());
        assertEquals("Because of type erasure", row.get("answer_text"));
        assertTrue(((String) row.get("question_snapshot")).contains("Original requirement"));
    }

    @Test
    @DisplayName("""
            Given a question still waiting for approval on its only concept
            When a learner sends an answer for it
            Then a not found response is returned, as the question isn't available for learning yet
            """)
    void submitAnswerReturnsNotFoundWhenQuestionIsNotAvailable() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        setConceptLinkStatus(questionId, "java-core", ConceptLinkStatus.PENDING);
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = submit("""
                {
                    "questionId": %d,
                    "answer": "Because of type erasure"
                }
                """.formatted(questionId));

        // Then
        response
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Question %d is not available for learning".formatted(questionId));
    }

    @Test
    @DisplayName("""
            Given a question available for learning
            When a learner sends a blank answer
            Then a bad request is returned and nothing is stored
            """)
    void submitAnswerReturnsBadRequestWhenAnswerIsBlank() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = submit("""
                {
                    "questionId": %d,
                    "answer": "   "
                }
                """.formatted(questionId));

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("answer is required");

        assertEquals(0, countAnswers());
    }

    private RestTestClient.ResponseSpec submit(String requestBody) {
        return restClient
                .post()
                .uri("/learning/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange();
    }
}
