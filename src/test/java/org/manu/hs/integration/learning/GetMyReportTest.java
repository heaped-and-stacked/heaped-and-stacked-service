package org.manu.hs.integration.learning;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.question.ReportReason;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * GET /learning/questions/{id}/reports/mine
 */
class GetMyReportTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a learner with open feedback on a question, and another learner's feedback on it
            When the learner sends a GET request for their own feedback on it
            Then their open feedback is returned: its reason, note and status, without the question
            """)
    void getMyReportReturnsTheLearnersOpenReport() {
        // Given
        Long questionId = availableQuestion();
        Long mine = insertReport(questionId, OTHER_USER, ReportReason.UNCLEAR);
        insertReport(questionId, ADMIN, ReportReason.PRAISE);
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = getMine(questionId);

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(mine.intValue())
                .jsonPath("$.reason").isEqualTo("UNCLEAR")
                .jsonPath("$.note").isEqualTo("The model answer is wrong")
                .jsonPath("$.status").isEqualTo("OPEN")
                .jsonPath("$.reportedAt").exists()
                .jsonPath("$.question").doesNotExist();
    }

    @Test
    @DisplayName("""
            Given a learner whose only feedback on a question was already decided
            When the learner sends a GET request for their own feedback on it
            Then no content is returned: only open feedback can still be changed
            """)
    void getMyReportReturnsNoContentWhenNothingIsOpen() {
        // Given
        Long questionId = availableQuestion();
        setReportStatus(insertReport(questionId, OTHER_USER), "RESOLVED", AUTHOR);
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = getMine(questionId);

        // Then
        response.expectStatus().isNoContent();
    }

    @Test
    @DisplayName("""
            Given a question with feedback on it
            When a visitor who is not signed in asks for their own feedback on it
            Then unauthorized is returned
            """)
    void getMyReportReturnsUnauthorizedForVisitors() {
        // Given
        Long questionId = availableQuestion();
        insertReport(questionId, OTHER_USER);
        actingAsVisitor();

        // When
        RestTestClient.ResponseSpec response = getMine(questionId);

        // Then
        response.expectStatus().isUnauthorized();
    }

    private Long availableQuestion() {
        insertConcept("java-core");
        return insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
    }

    private RestTestClient.ResponseSpec getMine(Long questionId) {
        return restClient
                .get()
                .uri("/learning/questions/{id}/reports/mine", questionId)
                .exchange();
    }
}
