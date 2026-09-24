package org.manu.hs.integration.question;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DELETE /content/questions/{id}
 */
class DeleteQuestionTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given the caller is the author of a draft question
            When a DELETE request is sent to the questions endpoint
            Then the question and its concept links are deleted, the concepts are kept, and HTTP 204 is returned
            """)
    void deleteQuestionReturnsNoContentWhenAuthorDeletesOwnDraftQuestion() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.DRAFT, AUTHOR, "java-core");
        actingAs(AUTHOR, false);

        // When
        RestTestClient.ResponseSpec response = delete(questionId);

        // Then
        response.expectStatus().isNoContent();

        assertQuestionDoesNotExist(questionId);
        assertEquals(List.of(), findConceptTagsForQuestion(questionId));
        assertEquals("java-core", findConceptByTag("java-core").getTag());
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(QuestionStatus.class)
    @DisplayName("""
            Given the caller is an admin
            When a DELETE request is sent for another author's question in any status
            Then the question is deleted and HTTP 204 is returned
            """)
    void deleteQuestionReturnsNoContentWhenAdminDeletesAnyQuestion(QuestionStatus status) {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(status, AUTHOR, "java-core");
        actingAs(ADMIN, true);

        // When
        RestTestClient.ResponseSpec response = delete(questionId);

        // Then
        response.expectStatus().isNoContent();

        assertQuestionDoesNotExist(questionId);
    }

    @Test
    @DisplayName("""
            Given the caller is the author but the question is no longer in draft status
            When a DELETE request is sent to the questions endpoint
            Then a forbidden response is returned and the question still exists
            """)
    void deleteQuestionReturnsForbiddenWhenAuthorDeletesNonDraftQuestion() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.ACTIVE, AUTHOR, "java-core");
        actingAs(AUTHOR, false);

        // When
        RestTestClient.ResponseSpec response = delete(questionId);

        // Then
        response.expectStatus().isForbidden();

        assertEquals(QuestionStatus.ACTIVE, findQuestionById(questionId).getStatus());
    }

    @Test
    @DisplayName("""
            Given the caller is not the author of the question
            When a DELETE request is sent to the questions endpoint
            Then a forbidden response is returned and the question still exists
            """)
    void deleteQuestionReturnsForbiddenWhenNonAuthorDeletesQuestion() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.DRAFT, AUTHOR, "java-core");
        actingAs(OTHER_USER, false);

        // When
        RestTestClient.ResponseSpec response = delete(questionId);

        // Then
        response.expectStatus().isForbidden();

        assertEquals(AUTHOR, findQuestionById(questionId).getCreatedBy());
    }

    @Test
    @DisplayName("""
            Given no question exists for the given id
            When a DELETE request is sent to the questions endpoint
            Then a not found response is returned
            """)
    void deleteQuestionReturnsNotFoundWhenQuestionDoesNotExist() {
        // When
        RestTestClient.ResponseSpec response = delete(999_999L);

        // Then
        response
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Question with id '999999' not found");
    }

    @Test
    @DisplayName("""
            Given an id that is not a number
            When a DELETE request is sent to the questions endpoint
            Then a bad request is returned with a message
            """)
    void deleteQuestionReturnsBadRequestWhenIdIsNotANumber() {
        // When
        RestTestClient.ResponseSpec response = restClient
                .delete()
                .uri("/content/questions/{id}", "abc")
                .exchange();

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isNotEmpty();
    }

    private RestTestClient.ResponseSpec delete(Long questionId) {
        return restClient
                .delete()
                .uri("/content/questions/{id}", questionId)
                .exchange();
    }
}
