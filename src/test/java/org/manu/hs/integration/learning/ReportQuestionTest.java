package org.manu.hs.integration.learning;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * POST /learning/questions/{id}/reports
 */
class ReportQuestionTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a question learners can answer
            When a learner reports it as wrong, with a note
            Then an open report is stored with their reason and note, and HTTP 201 is returned
            """)
    void reportQuestionReturnsCreatedAndStoresOpenReport() {
        // Given
        Long questionId = availableQuestion();
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = report(questionId, """
                {
                    "reason": "WRONG",
                    "note": "The model answer says HashMap is ordered"
                }
                """);

        // Then
        response.expectStatus().isCreated();

        List<Map<String, Object>> reports = findReportRows(questionId);
        assertEquals(1, reports.size());
        assertEquals(OTHER_USER, ((Number) reports.getFirst().get("reported_by")).longValue());
        assertEquals("WRONG", reports.getFirst().get("reason"));
        assertEquals("The model answer says HashMap is ordered", reports.getFirst().get("note"));
        assertEquals("OPEN", reports.getFirst().get("report_status"));
    }

    @Test
    @DisplayName("""
            Given a question learners can answer
            When a learner praises it, with a note
            Then open praise is stored with their note, and HTTP 201 is returned
            """)
    void reportQuestionReturnsCreatedAndStoresPraise() {
        // Given
        Long questionId = availableQuestion();
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = report(questionId, """
                {
                    "reason": "PRAISE",
                    "note": "More exercises like this one, please"
                }
                """);

        // Then
        response.expectStatus().isCreated();

        List<Map<String, Object>> reports = findReportRows(questionId);
        assertEquals(1, reports.size());
        assertEquals("PRAISE", reports.getFirst().get("reason"));
        assertEquals("More exercises like this one, please", reports.getFirst().get("note"));
        assertEquals("OPEN", reports.getFirst().get("report_status"));
    }

    @Test
    @DisplayName("""
            Given a learner who already has an open report on a question
            When they report it again
            Then a conflict is returned and no second report is stored
            """)
    void reportQuestionReturnsConflictWhenLearnerAlreadyReportedIt() {
        // Given
        Long questionId = availableQuestion();
        insertReport(questionId, OTHER_USER);
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = report(questionId, """
                {
                    "reason": "UNCLEAR"
                }
                """);

        // Then
        response
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("You already reported question %d".formatted(questionId));

        assertEquals(1, findReportRows(questionId).size());
    }

    @Test
    @DisplayName("""
            Given a learner whose earlier report on a question was dismissed
            When they report it again
            Then a new open report is stored, as only open reports count
            """)
    void reportQuestionAllowsNewReportOnceEarlierOneIsDecided() {
        // Given
        Long questionId = availableQuestion();
        Long earlierReportId = insertReport(questionId, OTHER_USER);
        setReportStatus(earlierReportId, "DISMISSED", AUTHOR);
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = report(questionId, """
                {
                    "reason": "DUPLICATE"
                }
                """);

        // Then
        response.expectStatus().isCreated();

        List<Map<String, Object>> reports = findReportRows(questionId);
        assertEquals(2, reports.size());
        assertEquals("OPEN", reports.get(1).get("report_status"));
    }

    @Test
    @DisplayName("""
            Given a question learners can't answer, as it is benched
            When a learner tries to report it
            Then a not found response is returned and nothing is stored
            """)
    void reportQuestionReturnsNotFoundWhenQuestionIsNotAvailable() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.RETIRED, AUTHOR, "java-core");
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = report(questionId, """
                {
                    "reason": "WRONG"
                }
                """);

        // Then
        response.expectStatus().isNotFound();

        assertTrue(findReportRows(questionId).isEmpty());
    }

    @Test
    @DisplayName("""
            Given a question learners can answer
            When a learner reports it without a reason
            Then a bad request is returned and nothing is stored
            """)
    void reportQuestionReturnsBadRequestWhenReasonIsMissing() {
        // Given
        Long questionId = availableQuestion();
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = report(questionId, """
                {
                    "note": "Something is off"
                }
                """);

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("reason is required");

        assertTrue(findReportRows(questionId).isEmpty());
    }

    @Test
    @DisplayName("""
            Given a question learners can answer
            When a learner reports it with a reason that doesn't exist
            Then a bad request is returned and nothing is stored
            """)
    void reportQuestionReturnsBadRequestWhenReasonIsUnknown() {
        // Given
        Long questionId = availableQuestion();
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = report(questionId, """
                {
                    "reason": "BORING"
                }
                """);

        // Then
        response.expectStatus().isBadRequest();

        assertTrue(findReportRows(questionId).isEmpty());
    }

    private Long availableQuestion() {
        insertConcept("java-core");
        return insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
    }

    private RestTestClient.ResponseSpec report(Long questionId, String requestBody) {
        return restClient
                .post()
                .uri("/learning/questions/{id}/reports", questionId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange();
    }
}
