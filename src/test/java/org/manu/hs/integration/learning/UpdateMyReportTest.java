package org.manu.hs.integration.learning;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.question.ReportReason;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * PUT /learning/questions/{id}/reports/mine
 */
class UpdateMyReportTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a learner with open feedback on a question
            When they send a PUT request with a new reason and note
            Then the same feedback is changed and returned, still open
            """)
    void updateMyReportChangesReasonAndNote() {
        // Given
        Long questionId = availableQuestion();
        Long reportId = insertReport(questionId, OTHER_USER, ReportReason.UNCLEAR);
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = updateMine(questionId, """
                {
                    "reason": "PRAISE",
                    "note": "On second thought: it's clear, and a good one"
                }
                """);

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(reportId.intValue())
                .jsonPath("$.reason").isEqualTo("PRAISE")
                .jsonPath("$.note").isEqualTo("On second thought: it's clear, and a good one")
                .jsonPath("$.status").isEqualTo("OPEN");

        Map<String, Object> row = findReportRow(reportId);
        assertEquals("PRAISE", row.get("reason"));
        assertEquals("On second thought: it's clear, and a good one", row.get("note"));
        assertEquals("OPEN", row.get("report_status"));
    }

    @Test
    @DisplayName("""
            Given a learner whose feedback on a question was already decided
            When they try to change it
            Then a not found response is returned and the decided feedback stays as it was
            """)
    void updateMyReportReturnsNotFoundWhenNothingIsOpen() {
        // Given
        Long questionId = availableQuestion();
        Long reportId = insertReport(questionId, OTHER_USER);
        setReportStatus(reportId, "DISMISSED", AUTHOR);
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = updateMine(questionId, """
                {
                    "reason": "UNCLEAR"
                }
                """);

        // Then
        response
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("You have no open feedback on question %d".formatted(questionId));

        assertEquals("WRONG", findReportRow(reportId).get("reason"));
    }

    @Test
    @DisplayName("""
            Given a learner with open feedback on a question
            When they try to change it without a reason
            Then a bad request is returned and the feedback stays as it was
            """)
    void updateMyReportReturnsBadRequestWhenReasonIsMissing() {
        // Given
        Long questionId = availableQuestion();
        Long reportId = insertReport(questionId, OTHER_USER);
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = updateMine(questionId, """
                {
                    "note": "Something else"
                }
                """);

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("reason is required");

        assertEquals("The model answer is wrong", findReportRow(reportId).get("note"));
    }

    private Long availableQuestion() {
        insertConcept("java-core");
        return insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
    }

    private RestTestClient.ResponseSpec updateMine(Long questionId, String requestBody) {
        return restClient
                .put()
                .uri("/learning/questions/{id}/reports/mine", questionId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange();
    }
}
