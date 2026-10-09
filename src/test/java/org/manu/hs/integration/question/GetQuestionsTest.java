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
        Long olderDraft = insertQuestion(QuestionStatus.DRAFT, AUTHOR, "java-core");
        insertQuestion(QuestionStatus.SUBMITTED, OTHER_USER, "java-core");
        Long newestRetired = insertQuestion(QuestionStatus.RETIRED, AUTHOR, "java-core");
        actingAs(AUTHOR);

        // When
        QuestionPageResponse page = getQuestions("");

        // Then
        assertEquals(List.of(newestRetired, olderDraft), idsOf(page));
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
        QuestionPageResponse page = getQuestions("?muscle=java-core");

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
