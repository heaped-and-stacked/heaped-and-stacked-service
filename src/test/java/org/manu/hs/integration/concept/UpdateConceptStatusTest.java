package org.manu.hs.integration.concept;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.manu.hs.api.content.dto.ConceptResponse;
import org.manu.hs.domain.concept.Concept;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * PUT /content/concepts/{tag}/status
 */
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
            Given the caller is an admin
            When a PUT request is sent to the concept status endpoint with any target status
            Then the status is changed (setting the same status again is allowed) and the admin is recorded as updater
            """)
    void updateConceptStatusReturnsOkForAnyTransitionWhenCallerIsAdmin(ConceptStatus from, ConceptStatus to) {
        // Given
        insertConcept("java-core", from);
        actingAs(ADMIN);

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
        assertEquals(ADMIN, updatedConcept.getUpdatedBy());

        // Verify database entry
        Concept dbRecord = findConceptByTag("java-core");
        assertEquals(to, dbRecord.getStatus());
        assertEquals(ADMIN, dbRecord.getUpdatedBy());
        assertEquals(AUTHOR, dbRecord.getCreatedBy());
    }

    @Test
    @DisplayName("""
            Given an active concept linked to a submitted question
            When an admin sends the concept back to DRAFT
            Then a conflict is returned and the concept stays active
            """)
    void updateConceptStatusReturnsConflictWhenActiveQuestionUsesItAndTargetIsDraft() {
        // Given
        insertConcept("java-core");
        insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(ADMIN);

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
            When an admin retires the concept
            Then the status is changed, since only drafts are hidden from submitted questions
            """)
    void updateConceptStatusReturnsOkWhenRetiringConceptUsedByActiveQuestion() {
        // Given
        insertConcept("java-core");
        insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = updateStatus("java-core", "RETIRED");

        // Then
        response.expectStatus().isOk();

        assertEquals(ConceptStatus.RETIRED, findConceptByTag("java-core").getStatus());
    }

    @Test
    @DisplayName("""
            Given an active concept linked only to a draft question
            When an admin sends the concept back to DRAFT
            Then the status is changed, since only submitted questions block it
            """)
    void updateConceptStatusReturnsOkWhenOnlyDraftQuestionsUseIt() {
        // Given
        insertConcept("java-core");
        insertQuestion(QuestionStatus.DRAFT, AUTHOR, "java-core");
        actingAs(ADMIN);

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
        insertConcept("java-core", ConceptStatus.DRAFT);
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = updateStatus("java-core", "ACTIVE");

        // Then
        response.expectStatus().isOk();

        assertEquals(ConceptStatus.ACTIVE, findConceptByTag("java-core").getStatus());
        assertEquals(AUTHOR, findConceptByTag("java-core").getUpdatedBy());
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
            "ACTIVE, DRAFT",
            "ACTIVE, RETIRED",
            "DRAFT,  RETIRED"
    })
    @DisplayName("""
            Given the owner of a concept, who is not an admin
            When they set any status other than ACTIVE
            Then a forbidden response is returned, as only admins unpublish or retire concepts
            """)
    void updateConceptStatusReturnsForbiddenWhenOwnerDoesMoreThanPublish(ConceptStatus from, ConceptStatus to) {
        // Given
        insertConcept("java-core", from);
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = updateStatus("java-core", to.name());

        // Then
        response
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Only an admin may unpublish or retire a concept");

        assertEquals(from, findConceptByTag("java-core").getStatus());
    }

    @Test
    @DisplayName("""
            Given a draft concept owned by someone else, and a caller who is not an admin
            When they set its status to ACTIVE
            Then a forbidden response is returned and the concept stays a draft
            """)
    void updateConceptStatusReturnsForbiddenWhenCallerIsNotOwner() {
        // Given
        insertConcept("java-core", ConceptStatus.DRAFT);
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
        insertConcept("java-core", ConceptStatus.DRAFT);
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
            When an admin sends a PUT request to the concept status endpoint
            Then a bad request is returned with a message
            """)
    void updateConceptStatusReturnsBadRequestWhenStatusIsUnknown() {
        // Given
        insertConcept("java-core", ConceptStatus.DRAFT);
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = updateStatus("java-core", "LIVE");

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isNotEmpty();
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
