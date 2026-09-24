package org.manu.hs.integration.question;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.api.content.dto.ReviewResponse;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.core.ParameterizedTypeReference;
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
        List<ReviewResponse> reviews = getReviews();

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
        List<ReviewResponse> reviews = getReviews();

        // Then
        assertEquals(List.of(waitingOnCore, waitingOnGenerics),
                reviews.stream().map(review -> review.getQuestion().getId()).toList());
    }

    @Test
    @DisplayName("""
            Given a question waiting for approval on someone else's concept
            When a member who owns no waiting concepts sends a GET request to the reviews endpoint
            Then an empty list is returned
            """)
    void getReviewsReturnsEmptyListWhenNothingWaitsOnOwnConcepts() {
        // Given
        insertConcept("java-core", "Java Core", "Fundamentals", List.of(), ConceptStatus.ACTIVE, AUTHOR);
        insertQuestionAwaitingApproval(OTHER_USER, "java-core");
        actingAs(OTHER_USER);

        // When
        List<ReviewResponse> reviews = getReviews();

        // Then
        assertEquals(List.of(), reviews);
    }

    private List<ReviewResponse> getReviews() {
        RestTestClient.ResponseSpec response = restClient
                .get()
                .uri("/content/reviews")
                .exchange();

        List<ReviewResponse> reviews = response
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<ReviewResponse>>() {
                })
                .returnResult()
                .getResponseBody();

        assertNotNull(reviews);
        return reviews;
    }
}
