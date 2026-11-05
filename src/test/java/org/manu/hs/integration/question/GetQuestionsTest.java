package org.manu.hs.integration.question;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.api.content.dto.QuestionPageResponse;
import org.manu.hs.api.content.dto.QuestionResponse;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.question.Vote;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * GET /content/questions
 */
class GetQuestionsTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given the caller, a member, and another member have submitted questions
            When the member sends a GET request to the questions endpoint
            Then only the caller's questions are returned, in every status, newest first, with totals
            """)
    void getQuestionsReturnsOnlyAMembersOwnQuestions() {
        // Given
        insertConcept("java-core");
        Long older = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        insertQuestion(QuestionStatus.SUBMITTED, OTHER_USER, "java-core");
        Long newestRetired = insertQuestion(QuestionStatus.RETIRED, AUTHOR, "java-core");
        actingAs(AUTHOR);

        // When
        QuestionPageResponse page = getQuestions("");

        // Then
        assertEquals(List.of(newestRetired, older), idsOf(page));
        assertEquals(2, page.getTotalItems());
        assertEquals(1, page.getTotalPages());
    }

    @Test
    @DisplayName("""
            Given two members have submitted questions, and one of them has votes
            When a staff member sends a GET request to the questions endpoint
            Then every member's questions are returned, newest first, with their vote totals
            """)
    void getQuestionsReturnsEveryQuestionToStaff() {
        // Given
        insertConcept("java-core");
        Long byAuthor = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        Long byOther = insertQuestion(QuestionStatus.SUBMITTED, OTHER_USER, "java-core");
        insertVote(byAuthor, OTHER_USER, Vote.UP);
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When
        QuestionPageResponse page = getQuestions("");

        // Then
        assertEquals(List.of(byOther, byAuthor), idsOf(page));
        assertEquals(1, page.getItems().get(1).getUpVotes());
    }

    @Test
    @DisplayName("""
            Given two members' questions in the gym's pool and one written by a staff member
            When the staff member asks for the questions as a member would (reviewer mode off)
            Then only their own question is returned, and counted
            """)
    void getQuestionsAsAMemberReturnsOnlyStaffsOwnQuestions() {
        // Given
        insertConcept("java-core");
        insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        insertQuestion(QuestionStatus.SUBMITTED, OTHER_USER, "java-core");
        insertUser(STAFF, UserRole.STAFF);
        Long ownQuestion = insertQuestion(QuestionStatus.SUBMITTED, STAFF, "java-core");
        actingAs(STAFF);

        // When
        QuestionPageResponse page = getQuestions("?asMember=true");

        // Then
        assertEquals(List.of(ownQuestion), idsOf(page));
        assertEquals(1, page.getTotalItems());
    }

    @Test
    @DisplayName("""
            Given questions on different concepts, one of them on two concepts
            When a GET request filters the questions endpoint by one concept
            Then the questions on that concept are returned, also those with it as a second concept
            """)
    void getQuestionsFiltersByConcept() {
        // Given
        insertConcept("java-core");
        insertConcept("databases");
        Long onlyCore = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        Long both = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core", "databases");
        insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "databases");
        actingAs(AUTHOR);

        // When
        QuestionPageResponse page = getQuestions("?concept=java-core");

        // Then
        assertEquals(List.of(both, onlyCore), idsOf(page));
        assertEquals(List.of("databases", "java-core"), page.getItems().getFirst().getConceptTags());
    }

    @Test
    @DisplayName("""
            Given questions with different titles, one containing a percent sign
            When a GET request searches the questions endpoint for part of a title, in another case
            Then only the questions whose title contains it are returned, and % is matched literally
            """)
    void getQuestionsSearchesTitlesIgnoringCase() {
        // Given
        insertConcept("java-core");
        Long idempotency = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        setQuestionTitle(idempotency, "Idempotency: four techniques");
        Long percent = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        setQuestionTitle(percent, "Availability: 99.9% per service");
        Long other = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        setQuestionTitle(other, "Locks: what synchronized guarantees");
        actingAs(AUTHOR);

        // When
        QuestionPageResponse idempotent = getQuestions("?search=IDEMPOTEN");
        QuestionPageResponse percentSign = getQuestions("?search={search}", "%");

        // Then
        assertEquals(List.of(idempotency), idsOf(idempotent));
        assertEquals(List.of(percent), idsOf(percentSign));
    }

    @Test
    @DisplayName("""
            Given the caller has five questions
            When a GET request asks the questions endpoint for the second page of two
            Then the third and fourth newest are returned, with the totals of all five
            """)
    void getQuestionsReturnsTheRequestedPage() {
        // Given
        insertConcept("java-core");
        insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        Long second = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        Long third = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(AUTHOR);

        // When
        QuestionPageResponse page = getQuestions("?page=1&size=2");

        // Then
        assertEquals(List.of(third, second), idsOf(page));
        assertEquals(1, page.getPage());
        assertEquals(2, page.getSize());
        assertEquals(5, page.getTotalItems());
        assertEquals(3, page.getTotalPages());
    }

    @Test
    @DisplayName("""
            Given the caller has a question
            When a GET request asks for a page so far out that page times size passes the int range
            Then an empty page is returned with the real totals, not a server error
            """)
    void getQuestionsReturnsAnEmptyPageFarBeyondTheEnd() {
        // Given
        insertConcept("java-core");
        insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(AUTHOR);

        // When
        QuestionPageResponse page = getQuestions("?page=21474837&size=100");

        // Then
        assertEquals(List.of(), idsOf(page));
        assertEquals(1, page.getTotalItems());
    }

    @Test
    @DisplayName("""
            Given a signed-in member
            When a GET request asks the questions endpoint for pages of 500
            Then a bad request is returned
            """)
    void getQuestionsReturnsBadRequestForAPageTooLarge() {
        // Given
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = request("?size=500");

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("size must be between 1 and 100");
    }

    @Test
    @DisplayName("""
            Given a visitor who isn't signed in
            When they send a GET request to the questions endpoint
            Then unauthorized is returned
            """)
    void getQuestionsReturnsUnauthorizedForVisitors() {
        // Given
        actingAsVisitor();

        // When
        RestTestClient.ResponseSpec response = request("");

        // Then
        response.expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("""
            Given a member's question on a gym concept and another on their own concept only
            When gym staff list the exercises
            Then only the one in the gym's pool is listed: a member's own concept is theirs to curate
            """)
    void getQuestionsListsOnlyTheGymsPoolToStaff() {
        // Given
        insertConcept("java-core");
        insertCustomConcept("my-katas", org.manu.hs.domain.concept.ConceptStatus.ACTIVE, AUTHOR);
        Long gymQuestion = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "my-katas");
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When
        QuestionPageResponse page = getQuestions("");

        // Then
        assertEquals(List.of(gymQuestion), idsOf(page));
    }

    @Test
    @DisplayName("""
            Given a member's own concept with exercises by others: one accepted, one waiting for
            approval, one declined; and another member's exercise on another concept
            When the concept's owner lists the exercises of their concept
            Then the accepted and the waiting one are listed, whoever wrote them: an owner curates
            every exercise on their concept; the declined one and the other concept's are not
            """)
    void getQuestionsListsEveryExerciseOnAConceptToItsOwner() {
        // Given
        insertCustomConcept("my-katas", org.manu.hs.domain.concept.ConceptStatus.ACTIVE, AUTHOR);
        insertCustomConcept("their-katas", org.manu.hs.domain.concept.ConceptStatus.ACTIVE, OTHER_USER);
        Long accepted = insertQuestion(QuestionStatus.SUBMITTED, OTHER_USER, "my-katas");
        Long waiting = insertQuestionAwaitingApproval(OTHER_USER, "my-katas");
        Long declined = insertQuestion(QuestionStatus.SUBMITTED, OTHER_USER, "my-katas");
        setConceptLinkStatus(declined, "my-katas", org.manu.hs.domain.question.ConceptLinkStatus.DECLINED);
        insertQuestion(QuestionStatus.SUBMITTED, OTHER_USER, "their-katas");
        actingAs(AUTHOR);

        // When
        QuestionPageResponse page = getQuestions("?concept=my-katas");

        // Then
        assertEquals(List.of(waiting, accepted), idsOf(page));
    }

    @Test
    @DisplayName("""
            Given a staff member and two questions in the gym's pool, only one of them verified
            When they ask for the verified questions, then for the unverified ones
            Then each answer holds only that one, and counts only that one
            """)
    void getQuestionsFiltersByVerified() {
        // Given
        insertConcept("java-core");
        Long verified = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        Long unverified = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        setQuestionVerifiedBy(verified, ADMIN);
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When
        QuestionPageResponse verifiedPage = getQuestions("?verified=true");
        QuestionPageResponse unverifiedPage = getQuestions("?verified=false");

        // Then
        assertEquals(List.of(verified), idsOf(verifiedPage));
        assertEquals(1, verifiedPage.getTotalItems());
        assertEquals(List.of(unverified), idsOf(unverifiedPage));
        assertEquals(1, unverifiedPage.getTotalItems());
    }

    private QuestionPageResponse getQuestions(String query, Object... uriVariables) {
        QuestionPageResponse page = request(query, uriVariables)
                .expectStatus().isOk()
                .expectBody(QuestionPageResponse.class)
                .returnResult()
                .getResponseBody();
        assertNotNull(page);
        return page;
    }

    private RestTestClient.ResponseSpec request(String query, Object... uriVariables) {
        return restClient
                .get()
                .uri("/content/questions" + query, uriVariables)
                .exchange();
    }

    private static List<Long> idsOf(QuestionPageResponse page) {
        return page.getItems().stream().map(QuestionResponse::getId).toList();
    }
}
