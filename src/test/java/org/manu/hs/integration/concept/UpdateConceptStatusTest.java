package org.manu.hs.integration.concept;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.manu.hs.api.content.dto.ConceptResponse;
import org.manu.hs.domain.concept.Concept;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * PUT /content/concepts/{tag}/status
 */
@ExtendWith(OutputCaptureExtension.class)
class UpdateConceptStatusTest extends AbstractIntegrationTest {

    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
            "DRAFT,   DRAFT",
            "DRAFT,   ACTIVE",
            "DRAFT,   RETIRED",
            "ACTIVE,  DRAFT",
            "ACTIVE,  ACTIVE",
            "ACTIVE,  RETIRED",
            "RETIRED, DRAFT",
            "RETIRED, ACTIVE",
            "RETIRED, RETIRED"
    })
    @DisplayName("""
            Given the caller owns the concept, their own (not featured)
            When a PUT request is sent to the concept status endpoint with any target status
            Then the status is changed (setting the same status again is allowed) and the owner is
            recorded as updater: an owner has full control of their own concept
            """)
    void updateConceptStatusReturnsOkForAnyTransitionWhenCallerOwnsIt(ConceptStatus from, ConceptStatus to) {
        // Given
        insertCustomConcept("java-core", from, AUTHOR);
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = updateStatus("java-core", to.name());

        // Then
        ConceptResponse updatedConcept = response
                .expectStatus().isOk()
                .expectBody(ConceptResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(updatedConcept);
        assertEquals(to, updatedConcept.getStatus());
        assertEquals(AUTHOR, updatedConcept.getUpdatedBy());

        // Verify database entry
        Concept dbRecord = findConceptByTag("java-core");
        assertEquals(to, dbRecord.getStatus());
        assertEquals(AUTHOR, dbRecord.getUpdatedBy());
        assertEquals(AUTHOR, dbRecord.getCreatedBy());
    }

    @Test
    @DisplayName("""
            Given an active concept linked to a submitted question
            When its owner sends the concept back to DRAFT
            Then a conflict is returned and the concept stays active
            """)
    void updateConceptStatusReturnsConflictWhenActiveQuestionUsesItAndTargetIsDraft() {
        // Given
        insertCustomConcept("java-core", ConceptStatus.ACTIVE, AUTHOR);
        insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = updateStatus("java-core", "DRAFT");

        // Then
        response
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Concept 'java-core' can't go back to draft while submitted questions use it");

        assertEquals(ConceptStatus.ACTIVE, findConceptByTag("java-core").getStatus());
    }

    @Test
    @DisplayName("""
            Given an active concept linked to a submitted question
            When its owner retires the concept
            Then the status is changed, since only drafts are hidden from submitted questions
            """)
    void updateConceptStatusReturnsOkWhenRetiringConceptUsedByActiveQuestion() {
        // Given
        insertCustomConcept("java-core", ConceptStatus.ACTIVE, AUTHOR);
        insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = updateStatus("java-core", "RETIRED");

        // Then
        response.expectStatus().isOk();

        assertEquals(ConceptStatus.RETIRED, findConceptByTag("java-core").getStatus());
    }

    @Test
    @DisplayName("""
            Given an active concept linked only to a benched question
            When its owner sends the concept back to DRAFT
            Then the status is changed, since only submitted questions block it
            """)
    void updateConceptStatusReturnsOkWhenOnlyBenchedQuestionsUseIt() {
        // Given
        insertCustomConcept("java-core", ConceptStatus.ACTIVE, AUTHOR);
        insertQuestion(QuestionStatus.RETIRED, AUTHOR, "java-core");
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = updateStatus("java-core", "DRAFT");

        // Then
        response.expectStatus().isOk();

        assertEquals(ConceptStatus.DRAFT, findConceptByTag("java-core").getStatus());
    }

    @Test
    @DisplayName("""
            Given the owner of a draft concept
            When they set its status to ACTIVE
            Then the concept is published with HTTP 200, without any review
            """)
    void updateConceptStatusReturnsOkWhenOwnerPublishesDraft() {
        // Given
        insertCustomConcept("java-core", ConceptStatus.DRAFT, AUTHOR);
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = updateStatus("java-core", "ACTIVE");

        // Then
        response.expectStatus().isOk();

        assertEquals(ConceptStatus.ACTIVE, findConceptByTag("java-core").getStatus());
        assertEquals(AUTHOR, findConceptByTag("java-core").getUpdatedBy());
    }

    @Test
    @DisplayName("""
            Given a draft concept owned by someone else, and a caller who is not an admin
            When they set its status to ACTIVE
            Then a forbidden response is returned and the concept stays a draft
            """)
    void updateConceptStatusReturnsForbiddenWhenCallerIsNotOwner() {
        // Given
        insertCustomConcept("java-core", ConceptStatus.DRAFT, AUTHOR);
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = updateStatus("java-core", "ACTIVE");

        // Then
        response.expectStatus().isForbidden();

        assertEquals(ConceptStatus.DRAFT, findConceptByTag("java-core").getStatus());
    }

    @Test
    @DisplayName("""
            Given no concept exists for the given tag and the caller is not an admin
            When a PUT request is sent to the concept status endpoint
            Then a not found response is returned, before permissions are checked
            """)
    void updateConceptStatusReturnsNotFoundWhenConceptDoesNotExist() {
        // Given
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = updateStatus("does-not-exist", "ACTIVE");

        // Then
        response
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Concept with tag 'does-not-exist' not found");
    }

    @Test
    @DisplayName("""
            Given a payload without a status
            When the owner sends a PUT request to the concept status endpoint
            Then a bad request is returned and the status is unchanged
            """)
    void updateConceptStatusReturnsBadRequestWhenStatusIsMissing() {
        // Given
        insertCustomConcept("java-core", ConceptStatus.DRAFT, AUTHOR);
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = restClient
                .put()
                .uri("/content/concepts/{tag}/status", "java-core")
                .contentType(MediaType.APPLICATION_JSON)
                .body("{}")
                .exchange();

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("status is required");

        assertEquals(ConceptStatus.DRAFT, findConceptByTag("java-core").getStatus());
    }

    @Test
    @DisplayName("""
            Given a payload with a status that does not exist
            When its owner sends a PUT request to the concept status endpoint
            Then a bad request is returned with a message
            """)
    void updateConceptStatusReturnsBadRequestWhenStatusIsUnknown() {
        // Given
        insertCustomConcept("java-core", ConceptStatus.DRAFT, AUTHOR);
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = updateStatus("java-core", "LIVE");

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isNotEmpty();
    }

    @Test
    @DisplayName("""
            Given a member's own published concept
            When gym staff retire it
            Then the status is changed with HTTP 200: staff may take any concept down (moderation)
            """)
    void updateConceptStatusReturnsOkWhenStaffRetiresConcept() {
        // Given
        insertCustomConcept("java-core", ConceptStatus.ACTIVE, AUTHOR);
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When
        RestTestClient.ResponseSpec response = updateStatus("java-core", "RETIRED");

        // Then
        response.expectStatus().isOk();

        assertEquals(ConceptStatus.RETIRED, findConceptByTag("java-core").getStatus());
    }

    @Test
    @DisplayName("""
            Given a member's own published concept
            When gym staff try to send it back to DRAFT
            Then HTTP 403 is returned and it stays published: staff only retire (or restore) a
            member's own concept; everything else is up to its owner
            """)
    void updateConceptStatusReturnsForbiddenWhenStaffUnpublishesAMembersConcept() {
        // Given
        insertCustomConcept("java-core", ConceptStatus.ACTIVE, AUTHOR);
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When
        RestTestClient.ResponseSpec response = updateStatus("java-core", "DRAFT");

        // Then
        response.expectStatus().isForbidden();
        assertEquals(ConceptStatus.ACTIVE, findConceptByTag("java-core").getStatus());
    }

    @ParameterizedTest(name = "{0}")
    @CsvSource({"DRAFT", "RETIRED"})
    @DisplayName("""
            Given a featured concept, in the gym's pool
            When its owner, and then gym staff, try to unpublish or retire it
            Then HTTP 409 is returned for both and it stays published: unfeature it first, so the
            gym's pool never shrinks by accident
            """)
    void updateConceptStatusReturnsConflictWhileFeatured(ConceptStatus to) {
        // Given
        insertConcept("java-core", ConceptStatus.ACTIVE);
        insertUser(STAFF, UserRole.STAFF);

        // When / Then
        actingAs(AUTHOR);
        updateStatus("java-core", to.name()).expectStatus().isEqualTo(HttpStatus.CONFLICT);
        actingAs(STAFF);
        updateStatus("java-core", to.name()).expectStatus().isEqualTo(HttpStatus.CONFLICT);

        assertEquals(ConceptStatus.ACTIVE, findConceptByTag("java-core").getStatus());
    }

    @Test
    @DisplayName("""
            Given an active concept
            When its owner retires it
            Then the audit log records who changed which concept's status, and to what
            """)
    void conceptStatusChangeIsAudited(CapturedOutput output) {
        // Given
        insertCustomConcept("java-core", ConceptStatus.ACTIVE, AUTHOR);
        actingAs(AUTHOR);

        // When
        updateStatus("java-core", "RETIRED").expectStatus().isOk();

        // Then
        assertTrue(output.getAll().contains("concept_status_changed by=1001 concept=java-core status=RETIRED"));
    }

    private RestTestClient.ResponseSpec updateStatus(String tag, String status) {
        return restClient
                .put()
                .uri("/content/concepts/{tag}/status", tag)
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "status": "%s"
                        }
                        """.formatted(status))
                .exchange();
    }
}
