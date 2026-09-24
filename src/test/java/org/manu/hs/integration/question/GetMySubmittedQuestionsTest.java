package org.manu.hs.integration.question;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.api.content.dto.QuestionResponse;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * GET /content/questions/mine
 */
class GetMySubmittedQuestionsTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given the caller and another member have submitted questions in various statuses
            When a GET request is sent to the questions mine endpoint
            Then only the caller's questions are returned, in every status, newest first
            """)
    void getMySubmittedQuestionsReturnsOnlyCallersQuestionsNewestFirst() {
        // Given
        insertConcept("java-core");
        Long olderDraft = insertQuestion(QuestionStatus.DRAFT, AUTHOR, "java-core");
        insertQuestion(QuestionStatus.DRAFT, OTHER_USER, "java-core");
        Long active = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        Long newestRetired = insertQuestion(QuestionStatus.RETIRED, AUTHOR, "java-core");
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = getMine();

        // Then
        List<QuestionResponse> questions = response
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<QuestionResponse>>() {
                })
                .returnResult()
                .getResponseBody();

        assertNotNull(questions);
        assertEquals(List.of(newestRetired, active, olderDraft), questions.stream().map(QuestionResponse::getId).toList());
    }

    @Test
    @DisplayName("""
            Given the caller has a question linked to several concepts
            When a GET request is sent to the questions mine endpoint
            Then the question is returned once, with all its concept tags
            """)
    void getMySubmittedQuestionsReturnsEachQuestionOnceWithAllItsConceptTags() {
        // Given
        insertConcept("java-core");
        insertConcept("java-generics");
        insertQuestion(QuestionStatus.DRAFT, AUTHOR, "java-core", "java-generics");
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = getMine();

        // Then
        List<QuestionResponse> questions = response
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<QuestionResponse>>() {
                })
                .returnResult()
                .getResponseBody();

        assertNotNull(questions);
        assertEquals(1, questions.size());
        assertEquals(List.of("java-core", "java-generics"), questions.getFirst().getConceptTags());
    }

    @Test
    @DisplayName("""
            Given the caller has not submitted any questions
            When a GET request is sent to the questions mine endpoint
            Then an empty list is returned with HTTP 200
            """)
    void getMySubmittedQuestionsReturnsEmptyListWhenNoneSubmitted() {
        // Given
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = getMine();

        // Then
        List<QuestionResponse> questions = response
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<QuestionResponse>>() {
                })
                .returnResult()
                .getResponseBody();

        assertNotNull(questions);
        assertTrue(questions.isEmpty());
    }

    private RestTestClient.ResponseSpec getMine() {
        return restClient
                .get()
                .uri("/content/questions/mine")
                .exchange();
    }
}
