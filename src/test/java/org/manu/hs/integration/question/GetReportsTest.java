package org.manu.hs.integration.question;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.Instant;
import java.util.List;

/**
 * GET /content/reports
 */
class GetReportsTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given open reports on a question on the caller's concept, on a question the caller wrote,
            and on a question that is neither, plus a dismissed report
            When the caller sends a GET request to the reports endpoint
            Then only the open reports on their concept's or their own questions are returned, oldest first
            """)
    void getReportsReturnsOpenReportsOnOwnConceptsAndOwnQuestionsOldestFirst() {
        // Given
        insertConcept("java-core", "Java Core", "Fundamentals", List.of(), ConceptStatus.ACTIVE, AUTHOR);
        insertConcept("java-generics", "Java Generics", "Generics", List.of(), ConceptStatus.ACTIVE, OTHER_USER);
        Long onOwnConcept = insertQuestion(QuestionStatus.SUBMITTED, OTHER_USER, "java-core");
        Long ownQuestion = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-generics");
        Long someoneElses = insertQuestion(QuestionStatus.SUBMITTED, OTHER_USER, "java-generics");
        Long onOwnConceptReport = insertReport(onOwnConcept, ADMIN);
        Long ownQuestionReport = insertReport(ownQuestion, OTHER_USER);
        insertReport(someoneElses, ADMIN);
        setReportStatus(insertReport(onOwnConcept, OTHER_USER), "DISMISSED", AUTHOR);
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = getReports();

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].id").isEqualTo(onOwnConceptReport.intValue())
                .jsonPath("$[0].question.id").isEqualTo(onOwnConcept.intValue())
                .jsonPath("$[1].id").isEqualTo(ownQuestionReport.intValue())
                .jsonPath("$[1].question.id").isEqualTo(ownQuestion.intValue());
    }

    @Test
    @DisplayName("""
            Given an open report on a question
            When its author sends a GET request to the reports endpoint
            Then the report says why and what is wrong, with the question, but not who reported it
            """)
    void getReportsReturnsReasonNoteAndQuestionButNotTheReporter() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        insertReport(questionId, OTHER_USER);
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = getReports();

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].reason").isEqualTo("WRONG")
                .jsonPath("$[0].note").isEqualTo("The model answer is wrong")
                .jsonPath("$[0].status").isEqualTo("OPEN")
                .jsonPath("$[0].reportedAt").exists()
                .jsonPath("$[0].question.title").isEqualTo("Original title")
                .jsonPath("$[0].reportedBy").doesNotExist();
    }

    @Test
    @DisplayName("""
            Given open reports on questions of different authors and concept owners
            When an admin sends a GET request to the reports endpoint
            Then every open report is returned
            """)
    void getReportsReturnsEveryOpenReportWhenCallerIsAdmin() {
        // Given
        insertConcept("java-core", "Java Core", "Fundamentals", List.of(), ConceptStatus.ACTIVE, AUTHOR);
        insertConcept("java-generics", "Java Generics", "Generics", List.of(), ConceptStatus.ACTIVE, OTHER_USER);
        insertReport(insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core"), OTHER_USER);
        insertReport(insertQuestion(QuestionStatus.SUBMITTED, OTHER_USER, "java-generics"), AUTHOR);
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = getReports();

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2);
    }

    @Test
    @DisplayName("""
            Given an open report on someone else's question, on someone else's concept
            When a member sends a GET request to the reports endpoint
            Then an empty list is returned
            """)
    void getReportsReturnsEmptyListWhenNothingConcernsTheCaller() {
        // Given
        insertConcept("java-core", "Java Core", "Fundamentals", List.of(), ConceptStatus.ACTIVE, AUTHOR);
        insertReport(insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core"), ADMIN);
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = getReports();

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    @DisplayName("""
            Given open reports on questions of different authors and concept owners
            When gym staff send a GET request to the reports endpoint
            Then every open report is returned
            """)
    void getReportsReturnsEveryOpenReportWhenCallerIsStaff() {
        // Given
        insertConcept("java-core", "Java Core", "Fundamentals", List.of(), ConceptStatus.ACTIVE, AUTHOR);
        insertConcept("java-generics", "Java Generics", "Generics", List.of(), ConceptStatus.ACTIVE, OTHER_USER);
        insertReport(insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core"), OTHER_USER);
        insertReport(insertQuestion(QuestionStatus.SUBMITTED, OTHER_USER, "java-generics"), AUTHOR);
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When
        RestTestClient.ResponseSpec response = getReports();

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2);
    }

    @Test
    @DisplayName("""
            Given a resolved and a dismissed report on the caller's question, an open one there,
            and a decided report on someone else's question
            When the caller sends a GET request to the reports endpoint for decided reports
            Then only the decided reports they could decide are returned, newest decision first,
            each with its outcome and when it was decided
            """)
    void getDecidedReportsReturnsTheCallersDecidedReportsNewestDecisionFirst() {
        // Given
        insertConcept("java-core", "Java Core", "Fundamentals", List.of(), ConceptStatus.ACTIVE, OTHER_USER);
        Long ownQuestion = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        Long someoneElses = insertQuestion(QuestionStatus.SUBMITTED, OTHER_USER, "java-core");
        Long resolvedEarlier = insertReport(ownQuestion, ADMIN);
        setReportStatus(resolvedEarlier, "RESOLVED", AUTHOR, Instant.parse("2026-10-01T09:00:00Z"));
        Long dismissedLater = insertReport(ownQuestion, OTHER_USER);
        setReportStatus(dismissedLater, "DISMISSED", AUTHOR, Instant.parse("2026-10-05T09:00:00Z"));
        insertReport(ownQuestion, ADMIN);
        setReportStatus(insertReport(someoneElses, ADMIN), "RESOLVED", OTHER_USER);
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = getReports("?decided=true");

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].id").isEqualTo(dismissedLater.intValue())
                .jsonPath("$[0].status").isEqualTo("DISMISSED")
                .jsonPath("$[0].resolvedAt").isEqualTo("2026-10-05T09:00:00Z")
                .jsonPath("$[1].id").isEqualTo(resolvedEarlier.intValue())
                .jsonPath("$[1].status").isEqualTo("RESOLVED");
    }

    @Test
    @DisplayName("""
            Given decided reports on questions of different authors and concept owners, and an open one
            When gym staff send a GET request to the reports endpoint for decided reports
            Then every decided report is returned, and no open one
            """)
    void getDecidedReportsReturnsEveryDecidedReportWhenCallerIsStaff() {
        // Given
        insertConcept("java-core", "Java Core", "Fundamentals", List.of(), ConceptStatus.ACTIVE, AUTHOR);
        insertConcept("java-generics", "Java Generics", "Generics", List.of(), ConceptStatus.ACTIVE, OTHER_USER);
        setReportStatus(insertReport(insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core"), OTHER_USER), "RESOLVED", AUTHOR);
        setReportStatus(insertReport(insertQuestion(QuestionStatus.SUBMITTED, OTHER_USER, "java-generics"), AUTHOR), "DISMISSED", OTHER_USER);
        insertReport(insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core"), OTHER_USER);
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When
        RestTestClient.ResponseSpec response = getReports("?decided=true");

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2);
    }

    private RestTestClient.ResponseSpec getReports() {
        return getReports("");
    }

    private RestTestClient.ResponseSpec getReports(String query) {
        return restClient
                .get()
                .uri("/content/reports" + query)
                .exchange();
    }
}
