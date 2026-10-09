package org.manu.hs.integration.question;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * PUT /content/reports/{id}
 */
@ExtendWith(OutputCaptureExtension.class)
class ResolveReportTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given an open report on the caller's question
            When they mark it resolved, having fixed the question
            Then the report is resolved, recording who decided and when, and HTTP 204 is returned
            """)
    void resolveReportReturnsNoContentWhenAuthorResolves() {
        // Given
        Long reportId = reportOnQuestionBy(AUTHOR);
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = decide(reportId, "RESOLVED");

        // Then
        response.expectStatus().isNoContent();

        Map<String, Object> row = findReportRow(reportId);
        assertEquals("RESOLVED", row.get("report_status"));
        assertEquals(AUTHOR, ((Number) row.get("resolved_by")).longValue());
        assertNotNull(row.get("resolved_at"));
    }

    @Test
    @DisplayName("""
            Given an open report on someone else's question, linked to the caller's concept
            When the concept's owner dismisses it
            Then the report is dismissed and HTTP 204 is returned
            """)
    void resolveReportReturnsNoContentWhenConceptOwnerDismisses() {
        // Given
        insertConcept("java-core", "Java Core", "Fundamentals", List.of(), ConceptStatus.ACTIVE, OTHER_USER);
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        Long reportId = insertReport(questionId, ADMIN);
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = decide(reportId, "DISMISSED");

        // Then
        response.expectStatus().isNoContent();

        assertEquals("DISMISSED", findReportRow(reportId).get("report_status"));
    }

    @Test
    @DisplayName("""
            Given an open report on a question the caller didn't write, on a concept they don't own
            When they try to dismiss it
            Then a forbidden response is returned and the report stays open
            """)
    void resolveReportReturnsForbiddenWhenCallerIsNeitherAuthorNorConceptOwner() {
        // Given
        Long reportId = reportOnQuestionBy(AUTHOR);
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = decide(reportId, "DISMISSED");

        // Then
        response.expectStatus().isForbidden();

        Map<String, Object> row = findReportRow(reportId);
        assertEquals("OPEN", row.get("report_status"));
        assertNull(row.get("resolved_by"));
    }

    @Test
    @DisplayName("""
            Given an open report on someone else's question
            When an admin dismisses it
            Then the report is dismissed and HTTP 204 is returned
            """)
    void resolveReportReturnsNoContentWhenAdminDismisses() {
        // Given
        Long reportId = reportOnQuestionBy(AUTHOR);
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = decide(reportId, "DISMISSED");

        // Then
        response.expectStatus().isNoContent();

        assertEquals("DISMISSED", findReportRow(reportId).get("report_status"));
    }

    @Test
    @DisplayName("""
            Given no report exists for the given id
            When a member tries to resolve it
            Then a not found response is returned
            """)
    void resolveReportReturnsNotFoundWhenReportDoesNotExist() {
        // When
        RestTestClient.ResponseSpec response = decide(999_999L, "RESOLVED");

        // Then
        response
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Report with id '999999' not found");
    }

    @Test
    @DisplayName("""
            Given an open report on the caller's question
            When they send OPEN as the decision
            Then a bad request is returned and the report stays undecided
            """)
    void resolveReportReturnsBadRequestWhenDecisionIsOpen() {
        // Given
        Long reportId = reportOnQuestionBy(AUTHOR);
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = decide(reportId, "OPEN");

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("status must be RESOLVED or DISMISSED");

        assertNull(findReportRow(reportId).get("resolved_by"));
    }

    @Test
    @DisplayName("""
            Given an open report on someone else's question, on someone else's concept
            When gym staff dismiss it
            Then the report is dismissed, recording staff as the decider, and HTTP 204 is returned
            """)
    void resolveReportReturnsNoContentWhenStaffDismisses() {
        // Given
        Long reportId = reportOnQuestionBy(AUTHOR);
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When
        RestTestClient.ResponseSpec response = decide(reportId, "DISMISSED");

        // Then
        response.expectStatus().isNoContent();

        Map<String, Object> row = findReportRow(reportId);
        assertEquals("DISMISSED", row.get("report_status"));
        assertEquals(STAFF, ((Number) row.get("resolved_by")).longValue());
    }

    @Test
    @DisplayName("""
            Given a report on the caller's question that staff already dismissed
            When the author tries to mark it resolved
            Then a conflict is returned and the first decision stays, with its decider: a decision
            is final, not overwritten by whoever comes last
            """)
    void resolveReportReturnsConflictWhenAlreadyDecided() {
        // Given
        Long reportId = reportOnQuestionBy(AUTHOR);
        insertUser(STAFF, UserRole.STAFF);
        setReportStatus(reportId, "DISMISSED", STAFF);
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = decide(reportId, "RESOLVED");

        // Then
        response
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Report '%d' was already decided".formatted(reportId));

        Map<String, Object> row = findReportRow(reportId);
        assertEquals("DISMISSED", row.get("report_status"));
        assertEquals(STAFF, ((Number) row.get("resolved_by")).longValue());
    }

    @Test
    @DisplayName("""
            Given an open report on the caller's question
            When they dismiss it
            Then the audit log records who decided which report, on which question, and how
            """)
    void reportDecisionIsAudited(CapturedOutput output) {
        // Given
        Long reportId = reportOnQuestionBy(AUTHOR);
        Long questionId = ((Number) findReportRow(reportId).get("question_id")).longValue();
        actingAs(AUTHOR);

        // When
        decide(reportId, "DISMISSED").expectStatus().isNoContent();

        // Then
        assertTrue(output.getAll().contains(
                "report_decided by=1001 report=%d question=%d status=DISMISSED".formatted(reportId, questionId)));
    }

    /** An open report, by ADMIN, on a question the author wrote on a concept nobody else owns. */
    private Long reportOnQuestionBy(Long author) {
        insertConcept("java-core", "Java Core", "Fundamentals", List.of(), ConceptStatus.ACTIVE, author);
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, author, "java-core");
        return insertReport(questionId, ADMIN);
    }

    private RestTestClient.ResponseSpec decide(Long reportId, String status) {
        return restClient
                .put()
                .uri("/content/reports/{id}", reportId)
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "status": "%s"
                        }
                        """.formatted(status))
                .exchange();
    }
}
