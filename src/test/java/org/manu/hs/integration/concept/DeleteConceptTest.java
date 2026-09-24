package org.manu.hs.integration.concept;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.manu.hs.domain.concept.ConceptStatus;
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
 * DELETE /content/concepts/{tag}
 */
@ExtendWith(OutputCaptureExtension.class)
class DeleteConceptTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a member's own published concept that no question uses
            When its owner sends a DELETE request
            Then the concept is deleted and HTTP 204 is returned: an owner has full control of
            their own concept
            """)
    void deleteConceptReturnsNoContentWhenOwnerDeletesOwnConcept() {
        // Given
        insertCustomConcept("java-core", ConceptStatus.ACTIVE, AUTHOR);
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = delete("java-core");

        // Then
        response.expectStatus().isNoContent();

        assertConceptDoesNotExist("java-core");
    }

    @Test
    @DisplayName("""
            Given the author of a draft concept that no question uses
            When they send a DELETE request
            Then the concept is deleted with HTTP 204, as authors may withdraw their own drafts
            """)
    void deleteConceptReturnsNoContentWhenAuthorDeletesOwnDraftConcept() {
        // Given
        insertCustomConcept("java-core", ConceptStatus.DRAFT, AUTHOR);
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = delete("java-core");

        // Then
        response.expectStatus().isNoContent();

        assertConceptDoesNotExist("java-core");
    }

    @Test
    @DisplayName("""
            Given the owner of a featured concept, in the gym's pool
            When they send a DELETE request
            Then HTTP 409 is returned and the concept is kept: unfeature it first
            """)
    void deleteConceptReturnsConflictWhileFeatured() {
        // Given
        insertConcept("java-core", ConceptStatus.ACTIVE);
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = delete("java-core");

        // Then
        response.expectStatus().isEqualTo(HttpStatus.CONFLICT);

        assertEquals("java-core", findConceptByTag("java-core").getTag());
    }

    @Test
    @DisplayName("""
            Given a draft concept written by someone else, and a caller who is a member
            When they send a DELETE request
            Then a forbidden response is returned and the concept is kept
            """)
    void deleteConceptReturnsForbiddenWhenNonAuthorDeletesConcept() {
        // Given
        insertCustomConcept("java-core", ConceptStatus.DRAFT, AUTHOR);
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = delete("java-core");

        // Then
        response.expectStatus().isForbidden();

        assertEquals("java-core", findConceptByTag("java-core").getTag());
    }

    @Test
    @DisplayName("""
            Given a concept that is linked to a question
            When a DELETE request is sent to the concepts endpoint
            Then a conflict is returned and both the concept and the link remain
            """)
    void deleteConceptReturnsConflictWhenAQuestionUsesIt() {
        // Given
        insertCustomConcept("java-core", ConceptStatus.ACTIVE, AUTHOR);
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(AUTHOR);

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

    @Test
    @DisplayName("""
            Given a member's own published concept, used by no question
            When gym staff send a DELETE request for it
            Then HTTP 403 is returned and the concept is kept: staff retire a member's concept if
            they must take it down; deleting it is its owner's decision
            """)
    void deleteConceptReturnsForbiddenWhenStaffDeletesAMembersConcept() {
        // Given
        insertCustomConcept("java-core", ConceptStatus.ACTIVE, AUTHOR);
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When
        RestTestClient.ResponseSpec response = delete("java-core");

        // Then
        response.expectStatus().isForbidden();

        assertEquals("java-core", findConceptByTag("java-core").getTag());
    }

    @Test
    @DisplayName("""
            Given a concept no question uses
            When its owner deletes it
            Then the audit log records who deleted which concept
            """)
    void conceptDeletionIsAudited(CapturedOutput output) {
        // Given
        insertCustomConcept("java-core", ConceptStatus.ACTIVE, AUTHOR);
        actingAs(AUTHOR);

        // When
        delete("java-core").expectStatus().isNoContent();

        // Then
        assertTrue(output.getAll().contains("concept_deleted by=1001 concept=java-core"));
    }

    private RestTestClient.ResponseSpec delete(String tag) {
        return restClient
                .delete()
                .uri("/content/concepts/{tag}", tag)
                .exchange();
    }
}
