package org.manu.hs.integration.question;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.api.content.dto.ReviewPageResponse;
import org.manu.hs.api.content.dto.ReviewResponse;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * GET /content/reviews
 */
class GetReviewsTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given questions waiting for approval on concepts of different owners, and one already accepted
            When a concept owner sends a GET request to the reviews endpoint
            Then only the questions waiting on their own concepts are returned, oldest first, with the concept
            """)
    void getReviewsReturnsQuestionsWaitingOnOwnConceptsOldestFirst() {
        // Given
        insertConcept("java-core", "Java Core", "Fundamentals", List.of(), ConceptStatus.ACTIVE, AUTHOR);
        insertConcept("java-generics", "Java Generics", "Generics", List.of(), ConceptStatus.ACTIVE, OTHER_USER);
        Long olderWaiting = insertQuestionAwaitingApproval(OTHER_USER, "java-core");
        insertQuestionAwaitingApproval(AUTHOR, "java-generics");
        Long newerWaiting = insertQuestionAwaitingApproval(OTHER_USER, "java-core");
        insertQuestion(QuestionStatus.SUBMITTED, OTHER_USER, "java-core");
        actingAs(AUTHOR);

        // When
        List<ReviewResponse> reviews = getReviews("").getItems();

        // Then
        assertEquals(List.of(olderWaiting, newerWaiting),
                reviews.stream().map(review -> review.getQuestion().getId()).toList());
        assertEquals(List.of("java-core", "java-core"),
                reviews.stream().map(ReviewResponse::getConceptTag).toList());
        assertEquals(OTHER_USER, reviews.getFirst().getQuestion().getCreatedBy());
    }

    @Test
    @DisplayName("""
            Given questions waiting for approval on concepts of different owners
            When an admin sends a GET request to the reviews endpoint
            Then every waiting question is returned, whoever owns the concept
            """)
    void getReviewsReturnsEveryWaitingQuestionWhenCallerIsAdmin() {
        // Given
        insertConcept("java-core", "Java Core", "Fundamentals", List.of(), ConceptStatus.ACTIVE, AUTHOR);
        insertConcept("java-generics", "Java Generics", "Generics", List.of(), ConceptStatus.ACTIVE, OTHER_USER);
        Long waitingOnCore = insertQuestionAwaitingApproval(OTHER_USER, "java-core");
        Long waitingOnGenerics = insertQuestionAwaitingApproval(AUTHOR, "java-generics");
        actingAs(ADMIN);

        // When
        List<ReviewResponse> reviews = getReviews("").getItems();

        // Then
        assertEquals(List.of(waitingOnCore, waitingOnGenerics),
                reviews.stream().map(review -> review.getQuestion().getId()).toList());
    }

    @Test
    @DisplayName("""
            Given a question waiting for approval on someone else's concept
            When a member who owns no waiting concepts sends a GET request to the reviews endpoint
            Then an empty page is returned
            """)
    void getReviewsReturnsEmptyListWhenNothingWaitsOnOwnConcepts() {
        // Given
        insertConcept("java-core", "Java Core", "Fundamentals", List.of(), ConceptStatus.ACTIVE, AUTHOR);
        insertQuestionAwaitingApproval(OTHER_USER, "java-core");
        actingAs(OTHER_USER);

        // When
        List<ReviewResponse> reviews = getReviews("").getItems();

        // Then
        assertEquals(List.of(), reviews);
    }

    @Test
    @DisplayName("""
            Given questions waiting for approval on concepts of different owners
            When gym staff send a GET request to the reviews endpoint
            Then every waiting question is returned, whoever owns the concept
            """)
    void getReviewsReturnsEveryWaitingQuestionWhenCallerIsStaff() {
        // Given
        insertConcept("java-core", "Java Core", "Fundamentals", List.of(), ConceptStatus.ACTIVE, AUTHOR);
        insertConcept("java-generics", "Java Generics", "Generics", List.of(), ConceptStatus.ACTIVE, OTHER_USER);
        Long waitingOnCore = insertQuestionAwaitingApproval(OTHER_USER, "java-core");
        Long waitingOnGenerics = insertQuestionAwaitingApproval(AUTHOR, "java-generics");
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When
        List<ReviewResponse> reviews = getReviews("").getItems();

        // Then
        assertEquals(List.of(waitingOnCore, waitingOnGenerics),
                reviews.stream().map(review -> review.getQuestion().getId()).toList());
    }

    @Test
    @DisplayName("""
            Given a question waiting for approval on a member's own concept, outside the gym's pool
            When gym staff ask what waits for them
            Then it isn't listed: approving exercises on a member's own concept is up to its owner
            """)
    void getReviewsLeavesMembersOwnConceptsOutForStaff() {
        // Given
        insertCustomConcept("my-katas", ConceptStatus.ACTIVE, AUTHOR);
        insertQuestionAwaitingApproval(OTHER_USER, "my-katas");
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When
        List<ReviewResponse> reviews = getReviews("").getItems();

        // Then
        assertEquals(List.of(), reviews);
    }

    @Test
    @DisplayName("""
            Given five questions waiting for approval on the caller's concept
            When the caller asks the reviews endpoint for the second page of two
            Then the third and fourth oldest are returned, with the totals across every page
            """)
    void getReviewsReturnsTheRequestedPage() {
        // Given
        insertConcept("java-core", "Java Core", "Fundamentals", List.of(), ConceptStatus.ACTIVE, AUTHOR);
        insertQuestionAwaitingApproval(OTHER_USER, "java-core");
        insertQuestionAwaitingApproval(OTHER_USER, "java-core");
        Long third = insertQuestionAwaitingApproval(OTHER_USER, "java-core");
        Long fourth = insertQuestionAwaitingApproval(OTHER_USER, "java-core");
        insertQuestionAwaitingApproval(OTHER_USER, "java-core");
        actingAs(AUTHOR);

        // When
        ReviewPageResponse page = getReviews("?page=1&size=2");

        // Then
        assertEquals(List.of(third, fourth),
                page.getItems().stream().map(review -> review.getQuestion().getId()).toList());
        assertEquals(1, page.getPage());
        assertEquals(2, page.getSize());
        assertEquals(5, page.getTotalItems());
        assertEquals(3, page.getTotalPages());
    }

    @Test
    @DisplayName("""
            Given a signed-in member
            When they ask the reviews endpoint for pages of 500
            Then a bad request is returned
            """)
    void getReviewsReturnsBadRequestForAPageTooLarge() {
        // Given
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = restClient
                .get()
                .uri("/content/reviews?size=500")
                .exchange();

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("size must be between 1 and 100");
    }

    private ReviewPageResponse getReviews(String query) {
        ReviewPageResponse page = restClient
                .get()
                .uri("/content/reviews" + query)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ReviewPageResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(page);
        return page;
    }
}
