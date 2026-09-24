package org.manu.hs.integration.question;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.manu.hs.api.content.dto.QuestionResponse;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.question.Question;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * PUT /content/questions/{id}/status
 */
class UpdateQuestionStatusTest extends AbstractIntegrationTest {

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
            When a PUT request is sent to the question status endpoint with any target status
            Then the status is changed (setting the same status again is allowed) and the admin is recorded as updater
            """)
    void updateQuestionStatusReturnsOkForAnyTransitionWhenCallerIsAdmin(QuestionStatus from, QuestionStatus to) {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(from, AUTHOR, "java-core");
        actingAs(ADMIN, true);

        // When
        RestTestClient.ResponseSpec response = updateStatus(questionId, to.name());

        // Then
        QuestionResponse updatedQuestion = response
                .expectStatus().isOk()
                .expectBody(QuestionResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(updatedQuestion);
        assertEquals(to, updatedQuestion.getStatus());
        assertEquals(ADMIN, updatedQuestion.getUpdatedBy());

        // Verify database entry
        Question dbRecord = findQuestionById(questionId);
        assertEquals(to, dbRecord.getStatus());
        assertEquals(ADMIN, dbRecord.getUpdatedBy());
        assertEquals(AUTHOR, dbRecord.getCreatedBy());
    }

    @Test
    @DisplayName("""
            Given a draft question linked to a concept that is still a draft
            When an admin sets the question status to ACTIVE
            Then a conflict is returned and the question stays a draft
            """)
    void updateQuestionStatusReturnsConflictWhenActivatingWithDraftConcept() {
        // Given
        insertConcept("java-core");
        insertConcept("java-records", ConceptStatus.DRAFT);
        Long questionId = insertQuestion(QuestionStatus.DRAFT, AUTHOR, "java-core", "java-records");
        actingAs(ADMIN, true);

        // When
        RestTestClient.ResponseSpec response = updateStatus(questionId, "ACTIVE");

        // Then
        response
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("A question can't be active while these concepts are drafts: java-records");

        assertEquals(QuestionStatus.DRAFT, findQuestionById(questionId).getStatus());
    }

    @ParameterizedTest(name = "to {0}")
    @EnumSource(value = QuestionStatus.class, names = {"DRAFT", "RETIRED"})
    @DisplayName("""
            Given an active question linked to a concept that is still a draft
            When an admin sends the question back to DRAFT or retires it
            Then the status is changed, since only going live requires approved concepts
            """)
    void updateQuestionStatusReturnsOkWhenLeavingActiveWithDraftConcept(QuestionStatus target) {
        // Given
        insertConcept("java-records", ConceptStatus.DRAFT);
        Long questionId = insertQuestion(QuestionStatus.ACTIVE, AUTHOR, "java-records");
        actingAs(ADMIN, true);

        // When
        RestTestClient.ResponseSpec response = updateStatus(questionId, target.name());

        // Then
        response.expectStatus().isOk();

        assertEquals(target, findQuestionById(questionId).getStatus());
    }

    @Test
    @DisplayName("""
            Given a draft question linked to a retired concept
            When an admin sets the question status to ACTIVE
            Then the status is changed, since only draft concepts block activation
            """)
    void updateQuestionStatusReturnsOkWhenActivatingWithRetiredConcept() {
        // Given
        insertConcept("java-core", ConceptStatus.RETIRED);
        Long questionId = insertQuestion(QuestionStatus.DRAFT, AUTHOR, "java-core");
        actingAs(ADMIN, true);

        // When
        RestTestClient.ResponseSpec response = updateStatus(questionId, "ACTIVE");

        // Then
        response.expectStatus().isOk();

        assertEquals(QuestionStatus.ACTIVE, findQuestionById(questionId).getStatus());
    }

    @Test
    @DisplayName("""
            Given the caller is not an admin, even though they are the author
            When a PUT request is sent to the question status endpoint
            Then a forbidden response is returned and the status is unchanged
            """)
    void updateQuestionStatusReturnsForbiddenWhenCallerIsNotAdmin() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.DRAFT, AUTHOR, "java-core");
        actingAs(AUTHOR, false);

        // When
        RestTestClient.ResponseSpec response = updateStatus(questionId, "ACTIVE");

        // Then
        response.expectStatus().isForbidden();

        assertEquals(QuestionStatus.DRAFT, findQuestionById(questionId).getStatus());
    }

    @Test
    @DisplayName("""
            Given no question exists for the given id and the caller is not an admin
            When a PUT request is sent to the question status endpoint
            Then a not found response is returned, before permissions are checked
            """)
    void updateQuestionStatusReturnsNotFoundWhenQuestionDoesNotExist() {
        // Given
        actingAs(AUTHOR, false);

        // When
        RestTestClient.ResponseSpec response = updateStatus(999_999L, "ACTIVE");

        // Then
        response.expectStatus().isNotFound();
    }

    @Test
    @DisplayName("""
            Given a payload without a status
            When an admin sends a PUT request to the question status endpoint
            Then a bad request is returned and the status is unchanged
            """)
    void updateQuestionStatusReturnsBadRequestWhenStatusIsMissing() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.DRAFT, AUTHOR, "java-core");
        actingAs(ADMIN, true);

        // When
        RestTestClient.ResponseSpec response = restClient
                .put()
                .uri("/content/questions/{id}/status", questionId)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{}")
                .exchange();

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("status is required");

        assertEquals(QuestionStatus.DRAFT, findQuestionById(questionId).getStatus());
    }

    @Test
    @DisplayName("""
            Given a payload with a status that does not exist
            When an admin sends a PUT request to the question status endpoint
            Then a bad request is returned with a message
            """)
    void updateQuestionStatusReturnsBadRequestWhenStatusIsUnknown() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.DRAFT, AUTHOR, "java-core");
        actingAs(ADMIN, true);

        // When
        RestTestClient.ResponseSpec response = updateStatus(questionId, "LIVE");

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isNotEmpty();
    }

    private RestTestClient.ResponseSpec updateStatus(Long questionId, String status) {
        return restClient
                .put()
                .uri("/content/questions/{id}/status", questionId)
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "status": "%s"
                        }
                        """.formatted(status))
                .exchange();
    }
}
