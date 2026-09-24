package org.manu.hs.integration.learning;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.api.learning.dto.AnswerResponse;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * GET /learning/answers
 */
class GetMyAnswersTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given answers by the learner and by someone else
            When the learner sends a GET request to the answers endpoint
            Then only their own answers are returned, newest first
            """)
    void getMyAnswersReturnsOwnAnswersNewestFirst() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(OTHER_USER);
        Long older = submitAnswer(questionId, "First try");
        Long newer = submitAnswer(questionId, "Second try");
        actingAs(AUTHOR);
        submitAnswer(questionId, "Someone else's answer");
        actingAs(OTHER_USER);

        // When
        List<AnswerResponse> answers = getMyAnswers();

        // Then
        assertEquals(List.of(newer, older), answers.stream().map(AnswerResponse::getId).toList());
    }

    @Test
    @DisplayName("""
            Given a learner's answer to a question that its author edited afterwards
            When the learner sends a GET request to the answers endpoint
            Then the answer still shows the question as it was when answered
            """)
    void getMyAnswersKeepsTheQuestionAsAnswered() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(OTHER_USER);
        submitAnswer(questionId, "Because of type erasure");
        setQuestionRequirement(questionId, "Reworded requirement");

        // When
        List<AnswerResponse> answers = getMyAnswers();

        // Then
        assertEquals("Original requirement", answers.getFirst().getQuestion().getRequirement());
    }

    @Test
    @DisplayName("""
            Given the learner answered two questions, one of them twice, and someone else answered it too
            When the learner sends a GET request to the answers endpoint for that one question
            Then only their own answers to it are returned, newest first, to compare past attempts
            """)
    void getMyAnswersReturnsOwnAnswersToOneQuestionWhenFiltered() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        Long otherQuestionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(OTHER_USER);
        Long older = submitAnswer(questionId, "First try");
        submitAnswer(otherQuestionId, "Another question");
        Long newer = submitAnswer(questionId, "Second try");
        actingAs(AUTHOR);
        submitAnswer(questionId, "Someone else's answer");
        actingAs(OTHER_USER);

        // When
        List<AnswerResponse> answers = getMyAnswers("?questionId=" + questionId);

        // Then
        assertEquals(List.of(newer, older), answers.stream().map(AnswerResponse::getId).toList());
    }

    private List<AnswerResponse> getMyAnswers() {
        return getMyAnswers("");
    }

    private List<AnswerResponse> getMyAnswers(String query) {
        RestTestClient.ResponseSpec response = restClient
                .get()
                .uri("/learning/answers" + query)
                .exchange();

        List<AnswerResponse> answers = response
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<AnswerResponse>>() {
                })
                .returnResult()
                .getResponseBody();

        assertNotNull(answers);
        return answers;
    }
}
