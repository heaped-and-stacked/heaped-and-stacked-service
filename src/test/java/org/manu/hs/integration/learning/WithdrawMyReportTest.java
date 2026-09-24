package org.manu.hs.integration.learning;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DELETE /learning/questions/{id}/reports/mine
 */
class WithdrawMyReportTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a learner with open feedback on a question, and another learner's feedback on it
            When they send a DELETE request for their own feedback
            Then theirs is gone, the other learner's stays, no content is returned,
            and they may send new feedback on the question
            """)
    void withdrawMyReportDeletesItAndAllowsANewOne() {
        // Given
        Long questionId = availableQuestion();
        insertReport(questionId, OTHER_USER);
        insertReport(questionId, ADMIN);
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = withdrawMine(questionId);

        // Then
        response.expectStatus().isNoContent();
        assertEquals(1, findReportRows(questionId).size());
        assertEquals(ADMIN, ((Number) findReportRows(questionId).getFirst().get("reported_by")).longValue());

        restClient
                .post()
                .uri("/learning/questions/{id}/reports", questionId)
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        { "reason": "PRAISE" }
                        """)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    @DisplayName("""
            Given a learner whose feedback on a question was already decided
            When they try to withdraw it
            Then a not found response is returned and the decided feedback stays
            """)
    void withdrawMyReportReturnsNotFoundWhenNothingIsOpen() {
        // Given
        Long questionId = availableQuestion();
        setReportStatus(insertReport(questionId, OTHER_USER), "RESOLVED", AUTHOR);
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = withdrawMine(questionId);

        // Then
        response
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("You have no open feedback on question %d".formatted(questionId));

        assertEquals(1, findReportRows(questionId).size());
    }

    private Long availableQuestion() {
        insertConcept("java-core");
        return insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
    }

    private RestTestClient.ResponseSpec withdrawMine(Long questionId) {
        return restClient
                .delete()
                .uri("/learning/questions/{id}/reports/mine", questionId)
                .exchange();
    }
}
