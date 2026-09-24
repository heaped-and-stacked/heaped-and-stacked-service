package org.manu.hs.integration.question;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.api.content.dto.QuestionResponse;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.question.Question;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.question.QuestionType;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * PUT /content/questions/{id}
 */
class UpdateQuestionTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given the caller is the author of a draft question
            When a PUT request is sent to the questions endpoint with a valid payload
            Then the question is updated, its concept links replaced, its status kept, and HTTP 200 returned
            """)
    void updateQuestionReturnsOkWhenAuthorEditsOwnDraftQuestion() {
        // Given
        insertConcept("java-core");
        insertConcept("java-generics");
        Long questionId = insertQuestion(QuestionStatus.DRAFT, AUTHOR, "java-core");
        actingAs(AUTHOR);

        String requestBody = """
                {
                    "requirement": "Updated requirement",
                    "sampleSolution": "Updated solution",
                    "type": "GOTCHA",
                    "difficultyScore": 4,
                    "expectedDurationMinutes": 8,
                    "source": "manu",
                    "conceptTags": ["java-generics"]
                }
                """;

        // When
        RestTestClient.ResponseSpec response = update(questionId, requestBody);

        // Then
        QuestionResponse updatedQuestion = response
                .expectStatus().isOk()
                .expectBody(QuestionResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(updatedQuestion);
        assertEquals("Updated requirement", updatedQuestion.getRequirement());
        assertEquals(QuestionType.GOTCHA, updatedQuestion.getType());
        assertEquals(4, updatedQuestion.getDifficultyScore());
        assertEquals(List.of("java-generics"), updatedQuestion.getConceptTags());
        assertEquals(QuestionStatus.DRAFT, updatedQuestion.getStatus());
        assertEquals(AUTHOR, updatedQuestion.getCreatedBy());
        assertEquals(AUTHOR, updatedQuestion.getUpdatedBy());

        // Verify database entry: the old link is gone, the new one is there
        assertEquals(List.of("java-generics"), findConceptTagsForQuestion(questionId));
    }

    @Test
    @DisplayName("""
            Given the caller is an admin
            When a PUT request is sent to the questions endpoint for another author's ACTIVE question
            Then the question is updated, keeps its author and status, and records the admin as updater
            """)
    void updateQuestionReturnsOkWhenAdminEditsAnyQuestion() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.ACTIVE, AUTHOR, "java-core");
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = update(questionId, validQuestionJson("java-core"));

        // Then
        QuestionResponse updatedQuestion = response
                .expectStatus().isOk()
                .expectBody(QuestionResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(updatedQuestion);
        assertEquals("Updated requirement", updatedQuestion.getRequirement());
        assertEquals(QuestionStatus.ACTIVE, updatedQuestion.getStatus());
        assertEquals(AUTHOR, updatedQuestion.getCreatedBy());
        assertEquals(ADMIN, updatedQuestion.getUpdatedBy());
    }

    @Test
    @DisplayName("""
            Given the author of a draft question links it to a concept that is still a draft
            When a PUT request is sent to the questions endpoint
            Then the question is updated, since a draft question may wait on a draft concept
            """)
    void updateQuestionReturnsOkWhenDraftQuestionLinksDraftConcept() {
        // Given
        insertConcept("java-core");
        insertConcept("java-records", ConceptStatus.DRAFT);
        Long questionId = insertQuestion(QuestionStatus.DRAFT, AUTHOR, "java-core");
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = update(questionId, validQuestionJson("java-records"));

        // Then
        response.expectStatus().isOk();

        assertEquals(List.of("java-records"), findConceptTagsForQuestion(questionId));
    }

    @Test
    @DisplayName("""
            Given an admin links an ACTIVE question to a concept that is still a draft
            When a PUT request is sent to the questions endpoint
            Then a conflict is returned and the question is unchanged
            """)
    void updateQuestionReturnsConflictWhenActiveQuestionLinksDraftConcept() {
        // Given
        insertConcept("java-core");
        insertConcept("java-records", ConceptStatus.DRAFT);
        Long questionId = insertQuestion(QuestionStatus.ACTIVE, AUTHOR, "java-core");
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = update(questionId, validQuestionJson("java-core", "java-records"));

        // Then
        response
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("A question can't be active while these concepts are drafts: java-records");

        assertEquals("Original requirement", findQuestionById(questionId).getRequirement());
        assertEquals(List.of("java-core"), findConceptTagsForQuestion(questionId));
    }

    @Test
    @DisplayName("""
            Given the caller is not the author of a draft question and is not an admin
            When a PUT request is sent to the questions endpoint, even with an invalid payload
            Then a forbidden response is returned and the question is unchanged
            """)
    void updateQuestionReturnsForbiddenWhenNonAuthorEditsDraftQuestion() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.DRAFT, AUTHOR, "java-core");
        actingAs(OTHER_USER);

        // Forbidden is reported before the payload is validated.
        String invalidRequestBody = """
                {
                    "sampleSolution": "No requirement"
                }
                """;

        // When
        RestTestClient.ResponseSpec response = update(questionId, invalidRequestBody);

        // Then
        response.expectStatus().isForbidden();

        assertEquals("Original requirement", findQuestionById(questionId).getRequirement());
    }

    @Test
    @DisplayName("""
            Given the caller is the author but the question is no longer in draft status
            When a PUT request is sent to the questions endpoint
            Then a forbidden response is returned and the question is unchanged
            """)
    void updateQuestionReturnsForbiddenWhenAuthorEditsNonDraftQuestion() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.ACTIVE, AUTHOR, "java-core");
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = update(questionId, validQuestionJson("java-core"));

        // Then
        response.expectStatus().isForbidden();

        assertEquals("Original requirement", findQuestionById(questionId).getRequirement());
    }

    @Test
    @DisplayName("""
            Given no question exists for the given id
            When a PUT request is sent to the questions endpoint, even with an invalid payload
            Then a not found response is returned
            """)
    void updateQuestionReturnsNotFoundWhenQuestionDoesNotExist() {
        // Given
        actingAs(OTHER_USER);

        // Not found is reported before permissions and payload are checked.
        String invalidRequestBody = """
                {
                    "sampleSolution": "No requirement"
                }
                """;

        // When
        RestTestClient.ResponseSpec response = update(999_999L, invalidRequestBody);

        // Then
        response
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Question with id '999999' not found");
    }

    @Test
    @DisplayName("""
            Given a payload with no concept tags
            When a PUT request is sent to the questions endpoint
            Then a bad request is returned and the question is unchanged
            """)
    void updateQuestionReturnsBadRequestWhenConceptTagsAreEmpty() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.DRAFT, AUTHOR, "java-core");
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = update(questionId, validQuestionJson());

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("conceptTags is required");

        Question unchanged = findQuestionById(questionId);
        assertEquals("Original requirement", unchanged.getRequirement());
        assertEquals(List.of("java-core"), findConceptTagsForQuestion(questionId));
    }

    private RestTestClient.ResponseSpec update(Long questionId, String requestBody) {
        return restClient
                .put()
                .uri("/content/questions/{id}", questionId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange();
    }
}
