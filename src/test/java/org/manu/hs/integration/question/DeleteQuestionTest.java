package org.manu.hs.integration.question;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * DELETE /content/questions/{id}
 */
@ExtendWith(OutputCaptureExtension.class)
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
        actingAs(AUTHOR);

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
        actingAs(ADMIN);

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
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = delete(questionId);

        // Then
        response.expectStatus().isForbidden();

        assertEquals(QuestionStatus.SUBMITTED, findQuestionById(questionId).getStatus());
    }

    @Test
    @DisplayName("""
            Given a question a learner has already answered
            When an admin sends a DELETE request for it
            Then a conflict is returned, since deleting it would erase the learner's history,
            and the question and the answer are kept
            """)
    void deleteQuestionReturnsConflictWhenQuestionHasAnswers() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(OTHER_USER);
        submitAnswer(questionId, "My answer");
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = delete(questionId);

        // Then
        response
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Question with id '%d' has answers: retire it instead".formatted(questionId));

        assertEquals(QuestionStatus.SUBMITTED, findQuestionById(questionId).getStatus());
        assertEquals(1, countAnswers());
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
        actingAs(OTHER_USER);

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

    @Test
    @DisplayName("""
            Given a submitted question by a member that nobody has answered
            When gym staff send a DELETE request for it
            Then the question is deleted and HTTP 204 is returned
            """)
    void deleteQuestionReturnsNoContentWhenStaffDeletesSubmittedQuestion() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When
        RestTestClient.ResponseSpec response = delete(questionId);

        // Then
        response.expectStatus().isNoContent();

        assertQuestionDoesNotExist(questionId);
    }

    @Test
    @DisplayName("""
            Given a question nobody has answered
            When an admin deletes it
            Then the audit log records who deleted which question
            """)
    void questionDeletionIsAudited(CapturedOutput output) {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(ADMIN);

        // When
        delete(questionId).expectStatus().isNoContent();

        // Then
        assertTrue(output.getAll().contains("question_deleted by=1003 question=" + questionId));
    }

    private RestTestClient.ResponseSpec delete(Long questionId) {
        return restClient
                .delete()
                .uri("/content/questions/{id}", questionId)
                .exchange();
    }
}
