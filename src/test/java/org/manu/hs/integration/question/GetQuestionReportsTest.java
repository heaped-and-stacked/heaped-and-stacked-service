package org.manu.hs.integration.question;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.question.ReportReason;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.Instant;
import java.util.List;

/**
 * GET /content/questions/{id}/reports
 */
class GetQuestionReportsTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a question with two decided reports, an open report and an open praise, and an open
            report on another question
            When its author sends a GET request for the question's reports
            Then only its four reports are returned: the open ones first, oldest first, then the
            decided ones, newest decision first
            """)
    void getQuestionReportsReturnsOpenOldestFirstThenDecidedNewestDecisionFirst() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        Long resolved = insertReport(questionId, ADMIN);
        setReportStatus(resolved, "RESOLVED", AUTHOR, Instant.parse("2026-10-01T09:00:00Z"));
        Long dismissed = insertReport(questionId, OTHER_USER);
        setReportStatus(dismissed, "DISMISSED", AUTHOR, Instant.parse("2026-10-05T09:00:00Z"));
        Long open = insertReport(questionId, ADMIN);
        Long praise = insertReport(questionId, OTHER_USER, ReportReason.PRAISE);
        Long otherQuestion = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        insertReport(otherQuestion, ADMIN);
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = getQuestionReports(questionId);

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(4)
                .jsonPath("$[*].id").isEqualTo(List.of(open.intValue(), praise.intValue(), dismissed.intValue(), resolved.intValue()))
                .jsonPath("$[0].status").isEqualTo("OPEN")
                .jsonPath("$[0].question.id").isEqualTo(questionId.intValue())
                .jsonPath("$[1].reason").isEqualTo("PRAISE")
                .jsonPath("$[2].status").isEqualTo("DISMISSED")
                .jsonPath("$[2].resolvedAt").isEqualTo("2026-10-05T09:00:00Z")
                .jsonPath("$[3].status").isEqualTo("RESOLVED");
    }

    @Test
    @DisplayName("""
            Given a member's question with an open report
            When staff send a GET request for its reports
            Then the report is returned, as staff curate every question
            """)
    void getQuestionReportsReturnsTheReportsOfAnyQuestionToStaff() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        insertReport(questionId, OTHER_USER);
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When / Then
        getQuestionReports(questionId)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1);
    }

    @Test
    @DisplayName("""
            Given a member's question on a concept another member owns, with an open report
            When the concept's owner sends a GET request for its reports
            Then a forbidden response is returned: the reports of one question are for whoever may
            change it; concept owners decide them through the reports list
            """)
    void getQuestionReportsReturnsForbiddenToAMemberWhoDidNotWriteIt() {
        // Given
        insertConcept("java-core", "Java Core", "Fundamentals", List.of(), ConceptStatus.ACTIVE, OTHER_USER);
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        insertReport(questionId, ADMIN);
        actingAs(OTHER_USER);

        // When / Then
        getQuestionReports(questionId).expectStatus().isForbidden();
    }

    @Test
    @DisplayName("""
            Given no question with id 999
            When a member sends a GET request for its reports
            Then not found is returned, naming the question
            """)
    void getQuestionReportsReturnsNotFoundForAnUnknownId() {
        // Given
        actingAs(AUTHOR);

        // When / Then
        getQuestionReports(999L)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Question with id '999' not found");
    }

    @Test
    @DisplayName("""
            Given a member's question with an open report
            When a visitor sends a GET request for its reports
            Then unauthorized is returned
            """)
    void getQuestionReportsReturnsUnauthorizedForVisitors() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        insertReport(questionId, OTHER_USER);
        actingAsVisitor();

        // When / Then
        getQuestionReports(questionId).expectStatus().isUnauthorized();
    }

    private RestTestClient.ResponseSpec getQuestionReports(Long id) {
        return restClient.get().uri("/content/questions/{id}/reports", id).exchange();
    }
}
