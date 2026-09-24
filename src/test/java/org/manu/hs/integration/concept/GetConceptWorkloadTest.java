package org.manu.hs.integration.concept;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * GET /content/concepts/workload: for each concept the caller curates (their own, and for staff
 * the gym's pool), how many exercises it has, how many are still to verify, and how many wait for
 * approval. What Coach's Corner shows next to each muscle.
 */
class GetConceptWorkloadTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a member's own concept with three live exercises (one verified, two not), one
            waiting for approval and one benched, and a concept of someone else's
            When the member asks for their workload
            Then only their concept is listed, with 3 exercises, 2 to verify and 1 waiting
            """)
    void getWorkloadCountsTheCallersConcepts() {
        // Given
        insertCustomConcept("my-katas", ConceptStatus.ACTIVE, AUTHOR);
        insertCustomConcept("their-katas", ConceptStatus.ACTIVE, OTHER_USER);
        Long verified = insertQuestion(QuestionStatus.SUBMITTED, OTHER_USER, "my-katas");
        setQuestionVerifiedBy(verified, AUTHOR);
        insertQuestion(QuestionStatus.SUBMITTED, OTHER_USER, "my-katas");
        insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "my-katas");
        insertQuestionAwaitingApproval(OTHER_USER, "my-katas");
        insertQuestion(QuestionStatus.RETIRED, OTHER_USER, "my-katas");
        insertQuestion(QuestionStatus.SUBMITTED, OTHER_USER, "their-katas");
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = getWorkload();

        // Then
        response.expectStatus().isOk().expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].tag").isEqualTo("my-katas")
                .jsonPath("$[0].questions").isEqualTo(3)
                .jsonPath("$[0].toVerify").isEqualTo(2)
                .jsonPath("$[0].waiting").isEqualTo(1);
    }

    @Test
    @DisplayName("""
            Given a gym concept with no exercises yet, and a member's own concept
            When gym staff ask for their workload
            Then the gym concept is listed with zeros, and the member's isn't: staff curate the
            gym's pool
            """)
    void getWorkloadListsTheGymsPoolToStaff() {
        // Given
        insertConcept("java-core");
        insertCustomConcept("my-katas", ConceptStatus.ACTIVE, AUTHOR);
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When
        RestTestClient.ResponseSpec response = getWorkload();

        // Then
        response.expectStatus().isOk().expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].tag").isEqualTo("java-core")
                .jsonPath("$[0].questions").isEqualTo(0)
                .jsonPath("$[0].toVerify").isEqualTo(0)
                .jsonPath("$[0].waiting").isEqualTo(0);
    }

    @Test
    @DisplayName("""
            Given a visitor who isn't signed in
            When they ask for a workload
            Then HTTP 401 is returned
            """)
    void getWorkloadReturnsUnauthorizedForVisitors() {
        // Given
        actingAsVisitor();

        // When / Then
        getWorkload().expectStatus().isUnauthorized();
    }

    private RestTestClient.ResponseSpec getWorkload() {
        return restClient.get().uri("/content/concepts/workload").exchange();
    }
}
