package org.manu.hs.integration.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.api.user.dto.OverviewResponse;
import org.manu.hs.domain.question.ConceptLinkStatus;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.question.ReportReason;
import org.manu.hs.domain.user.AuthProvider;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.domain.user.UserStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * GET /admin/overview: the numbers at the top of the admin page.
 */
class GetOverviewTest extends AbstractIntegrationTest {

    private static final Long HOUSE = 1005L;
    private static final Long BLOCKED_MEMBER = 1006L;
    private static final Long DELETED_MEMBER = 1007L;
    private static final Long RENAMED_MEMBER = 1008L;

    @Test
    @DisplayName("""
            Given the three default users, a blocked member, a deleted account, the house account, and a
            member who renamed themselves like the house account, all joined just now
            When the admin sends a GET request to the overview endpoint
            Then the blocked and the renamed member count as users and new users; the deleted account
            and the house account (no sign-in linked) don't
            """)
    void getOverviewCountsUsersWithoutDeletedAccountsOrTheHouseAccount() {
        // Given
        insertUser(BLOCKED_MEMBER, UserRole.MEMBER, UserStatus.BLOCKED);
        insertUser(DELETED_MEMBER, UserRole.MEMBER, UserStatus.DELETED);
        insertHouseAccount(HOUSE);
        insertHouseAccount(RENAMED_MEMBER);
        insertIdentity(RENAMED_MEMBER, AuthProvider.GITHUB, "5001", "renamed-member");
        actingAs(ADMIN);

        // When
        OverviewResponse overview = getOverview();

        // Then
        assertEquals(5, overview.getUsers());
        assertEquals(5, overview.getNewUsersThisWeek());
    }

    @Test
    @DisplayName("""
            Given a user who joined 30 days ago and two who joined just now
            When the admin sends a GET request to the overview endpoint
            Then all three are users, and only the two are new this week (the last 7 days)
            """)
    void getOverviewCountsOnlyUsersWhoJoinedInTheLastSevenDaysAsNew() {
        // Given
        setUserCreatedAt(AUTHOR, Instant.now().minus(30, ChronoUnit.DAYS));
        actingAs(ADMIN);

        // When
        OverviewResponse overview = getOverview();

        // Then
        assertEquals(3, overview.getUsers());
        assertEquals(2, overview.getNewUsersThisWeek());
    }

    @Test
    @DisplayName("""
            Given a user with two answers this week, and another whose only answer is 8 days old
            When the admin sends a GET request to the overview endpoint
            Then 2 answers this week are counted, from 1 active user
            """)
    void getOverviewCountsAnswersAndAnsweringUsersOfTheLastSevenDays() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(AUTHOR);
        submitAnswer(questionId, "First answer");
        submitAnswer(questionId, "Second answer");
        actingAs(OTHER_USER);
        Long old = submitAnswer(questionId, "Old answer");
        setAnsweredAt(old, Instant.now().minus(8, ChronoUnit.DAYS));
        actingAs(ADMIN);

        // When
        OverviewResponse overview = getOverview();

        // Then
        assertEquals(1, overview.getActiveUsersThisWeek());
        assertEquals(2, overview.getAnswersThisWeek());
    }

    @Test
    @DisplayName("""
            Given an open problem report, an open praise and a resolved report on a question
            When the admin sends a GET request to the overview endpoint
            Then 2 open reports are counted, praise included, the resolved one not
            """)
    void getOverviewCountsOpenReportsOnly() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        Long resolved = insertReport(questionId, OTHER_USER);
        setReportStatus(resolved, "RESOLVED", ADMIN);
        insertReport(questionId, OTHER_USER);
        insertReport(questionId, ADMIN, ReportReason.PRAISE);
        actingAs(ADMIN);

        // When
        OverviewResponse overview = getOverview();

        // Then
        assertEquals(2, overview.getOpenReports());
    }

    @Test
    @DisplayName("""
            Given a submitted question waiting on two concepts, another waiting on one, a retired
            question still waiting, and an accepted question
            When the admin sends a GET request to the overview endpoint
            Then 2 questions awaiting review: each submitted one once, as the reviews list shows them
            """)
    void getOverviewCountsEachSubmittedQuestionAwaitingReviewOnce() {
        // Given
        insertConcept("java-core");
        insertConcept("java-generics");
        Long onTwo = insertQuestion(QuestionStatus.SUBMITTED, OTHER_USER, "java-core", "java-generics");
        setConceptLinkStatus(onTwo, "java-core", ConceptLinkStatus.PENDING);
        setConceptLinkStatus(onTwo, "java-generics", ConceptLinkStatus.PENDING);
        insertQuestionAwaitingApproval(OTHER_USER, "java-core");
        Long retired = insertQuestion(QuestionStatus.RETIRED, OTHER_USER, "java-core");
        setConceptLinkStatus(retired, "java-core", ConceptLinkStatus.PENDING);
        insertQuestion(QuestionStatus.SUBMITTED, OTHER_USER, "java-core");
        actingAs(ADMIN);

        // When
        OverviewResponse overview = getOverview();

        // Then
        assertEquals(2, overview.getQuestionsAwaitingReview());
    }

    @Test
    @DisplayName("""
            Given a signed-in member who is not an admin
            When a GET request is sent to the overview endpoint
            Then a forbidden response is returned
            """)
    void getOverviewReturnsForbiddenWhenCallerIsNotAdmin() {
        // Given
        actingAs(AUTHOR);

        // When / Then
        request()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Access denied");
    }

    @Test
    @DisplayName("""
            Given signed-in staff, who curate content but don't manage people
            When a GET request is sent to the overview endpoint
            Then a forbidden response is returned
            """)
    void getOverviewReturnsForbiddenWhenCallerIsStaff() {
        // Given
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When / Then
        request().expectStatus().isForbidden();
    }

    @Test
    @DisplayName("""
            Given a visitor who is not signed in
            When a GET request is sent to the overview endpoint
            Then an unauthorized response is returned
            """)
    void getOverviewReturnsUnauthorizedWhenVisitor() {
        // Given
        actingAsVisitor();

        // When / Then
        request().expectStatus().isUnauthorized();
    }

    private RestTestClient.ResponseSpec request() {
        return restClient.get().uri("/admin/overview").exchange();
    }

    @Test
    @DisplayName("""
            Given one exercise waiting on a gym concept and one on a member's own concept
            When an admin sends a GET request to the overview endpoint
            Then only the first counts: the other is its owner's to decide, not the gym's
            """)
    void getOverviewCountsOnlyWhatStaffCanDecide() {
        // Given
        insertConcept("java-core");
        insertCustomConcept("my-katas", org.manu.hs.domain.concept.ConceptStatus.ACTIVE, AUTHOR);
        insertQuestionAwaitingApproval(OTHER_USER, "java-core");
        insertQuestionAwaitingApproval(OTHER_USER, "my-katas");
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = clientSignedInAs(ADMIN).get().uri("/admin/overview").exchange();

        // Then
        response.expectStatus().isOk()
                .expectBody()
                .jsonPath("$.questionsAwaitingReview").isEqualTo(1);
    }

    private OverviewResponse getOverview() {
        OverviewResponse overview = request()
                .expectStatus().isOk()
                .expectBody(OverviewResponse.class)
                .returnResult()
                .getResponseBody();
        assertNotNull(overview);
        return overview;
    }
}
