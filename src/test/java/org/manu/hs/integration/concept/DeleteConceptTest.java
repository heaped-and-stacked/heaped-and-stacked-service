package org.manu.hs.integration.concept;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DELETE /content/concepts/{tag}
 */
class DeleteConceptTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a concept exists for the given tag and no question uses it
            When a DELETE request is sent to the concepts endpoint
            Then the concept is deleted and HTTP 204 is returned
            """)
    void deleteConceptReturnsNoContentWhenConceptExists() {
        // Given
        insertConcept("java-core");
        actingAs(ADMIN, true);

        // When
        RestTestClient.ResponseSpec response = delete("java-core");

        // Then
        response.expectStatus().isNoContent();

        assertConceptDoesNotExist("java-core");
    }

    @Test
    @DisplayName("""
            Given a concept that is linked to a question
            When a DELETE request is sent to the concepts endpoint
            Then a conflict is returned and both the concept and the link remain
            """)
    void deleteConceptReturnsConflictWhenAQuestionUsesIt() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.ACTIVE, AUTHOR, "java-core");
        actingAs(ADMIN, true);

        // When
        RestTestClient.ResponseSpec response = delete("java-core");

        // Then
        response
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Concept with tag 'java-core' is used by at least one question");

        assertEquals("java-core", findConceptByTag("java-core").getTag());
        assertEquals(List.of("java-core"), findConceptTagsForQuestion(questionId));
    }

    @Test
    @DisplayName("""
            Given no concept exists for the given tag
            When a DELETE request is sent to the concepts endpoint
            Then a not found response is returned
            """)
    void deleteConceptReturnsNotFoundWhenConceptDoesNotExist() {
        // When
        RestTestClient.ResponseSpec response = delete("does-not-exist");

        // Then
        response
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Concept with tag 'does-not-exist' not found");
    }

    private RestTestClient.ResponseSpec delete(String tag) {
        return restClient
                .delete()
                .uri("/content/concepts/{tag}", tag)
                .exchange();
    }
}
