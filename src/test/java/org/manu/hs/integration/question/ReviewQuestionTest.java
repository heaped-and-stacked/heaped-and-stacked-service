package org.manu.hs.integration.question;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.question.ConceptLinkStatus;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * PUT /content/questions/{id}/concepts/{tag}/review
 */
class ReviewQuestionTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a question waiting for approval on a concept, and linked to a second concept
            When the concept's owner accepts it for their concept
            Then HTTP 204 is returned, the question is accepted for that concept only, and the owner
            is recorded as the one who decided
            """)
    void reviewQuestionReturnsNoContentWhenOwnerAccepts() {
        // Given
        insertConcept("java-core", "Java Core", "Fundamentals", List.of(), ConceptStatus.ACTIVE, AUTHOR);
        insertConcept("java-generics", "Java Generics", "Generics", List.of(), ConceptStatus.ACTIVE, ADMIN);
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, OTHER_USER, "java-core", "java-generics");
        setConceptLinkStatus(questionId, "java-core", ConceptLinkStatus.PENDING);
        setConceptLinkStatus(questionId, "java-generics", ConceptLinkStatus.PENDING);
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = review(questionId, "java-core", "ACCEPTED");

        // Then
        response.expectStatus().isNoContent();

        assertEquals(ConceptLinkStatus.ACCEPTED, findConceptLinkStatus(questionId, "java-core"));
        assertEquals(AUTHOR, findConceptLinkDecidedBy(questionId, "java-core"));
        assertEquals(ConceptLinkStatus.PENDING, findConceptLinkStatus(questionId, "java-generics"));
    }

    @Test
    @DisplayName("""
            Given a question already accepted for a concept, for example a duplicate spotted later
            When the concept's owner declines it for their concept
            Then HTTP 204 is returned and the question is declined for that concept, without being deleted
            """)
    void reviewQuestionReturnsNoContentWhenOwnerDeclinesLater() {
        // Given
        insertConcept("java-core", "Java Core", "Fundamentals", List.of(), ConceptStatus.ACTIVE, AUTHOR);
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, OTHER_USER, "java-core");
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = review(questionId, "java-core", "DECLINED");

        // Then
        response.expectStatus().isNoContent();

        assertEquals(ConceptLinkStatus.DECLINED, findConceptLinkStatus(questionId, "java-core"));
        assertEquals(1, countQuestions());
    }

    @Test
    @DisplayName("""
            Given a question waiting for approval on someone else's concept
            When an admin accepts it
            Then HTTP 204 is returned and the question is accepted, with the admin recorded
            """)
    void reviewQuestionReturnsNoContentWhenAdminDecides() {
        // Given
        insertConcept("java-core", "Java Core", "Fundamentals", List.of(), ConceptStatus.ACTIVE, AUTHOR);
        Long questionId = insertQuestionAwaitingApproval(OTHER_USER, "java-core");
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = review(questionId, "java-core", "ACCEPTED");

        // Then
        response.expectStatus().isNoContent();

        assertEquals(ConceptLinkStatus.ACCEPTED, findConceptLinkStatus(questionId, "java-core"));
        assertEquals(ADMIN, findConceptLinkDecidedBy(questionId, "java-core"));
    }

    @Test
    @DisplayName("""
            Given a question waiting for approval on a concept, and a caller who neither owns the concept
            nor is an admin, not even the question's author
            When they try to accept it
            Then a forbidden response is returned and the question keeps waiting
            """)
    void reviewQuestionReturnsForbiddenWhenCallerDoesNotOwnConcept() {
        // Given
        insertConcept("java-core", "Java Core", "Fundamentals", List.of(), ConceptStatus.ACTIVE, AUTHOR);
        Long questionId = insertQuestionAwaitingApproval(OTHER_USER, "java-core");
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = review(questionId, "java-core", "ACCEPTED");

        // Then
        response.expectStatus().isForbidden();

        assertEquals(ConceptLinkStatus.PENDING, findConceptLinkStatus(questionId, "java-core"));
    }

    @Test
    @DisplayName("""
            Given no question exists for the given id
            When a review is sent
            Then a not found response is returned
            """)
    void reviewQuestionReturnsNotFoundWhenQuestionDoesNotExist() {
        // Given
        insertConcept("java-core", "Java Core", "Fundamentals", List.of(), ConceptStatus.ACTIVE, AUTHOR);
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = review(999_999L, "java-core", "ACCEPTED");

        // Then
        response.expectStatus().isNotFound();
    }

    @Test
    @DisplayName("""
            Given a question that isn't linked to the given concept
            When the concept's owner sends a review for that pair
            Then a not found response is returned with a message
            """)
    void reviewQuestionReturnsNotFoundWhenQuestionIsNotLinkedToConcept() {
        // Given
        insertConcept("java-core", "Java Core", "Fundamentals", List.of(), ConceptStatus.ACTIVE, AUTHOR);
        insertConcept("java-generics", "Java Generics", "Generics", List.of(), ConceptStatus.ACTIVE, AUTHOR);
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, OTHER_USER, "java-core");
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = review(questionId, "java-generics", "ACCEPTED");

        // Then
        response
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Question %d is not linked to concept 'java-generics'".formatted(questionId));
    }

    @Test
    @DisplayName("""
            Given a question waiting for approval
            When its concept's owner sends a review without a status
            Then a bad request is returned and the question keeps waiting
            """)
    void reviewQuestionReturnsBadRequestWhenStatusIsMissing() {
        // Given
        insertConcept("java-core", "Java Core", "Fundamentals", List.of(), ConceptStatus.ACTIVE, AUTHOR);
        Long questionId = insertQuestionAwaitingApproval(OTHER_USER, "java-core");
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = restClient
                .put()
                .uri("/content/questions/{id}/concepts/{tag}/review", questionId, "java-core")
                .contentType(MediaType.APPLICATION_JSON)
                .body("{}")
                .exchange();

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("status is required");

        assertEquals(ConceptLinkStatus.PENDING, findConceptLinkStatus(questionId, "java-core"));
    }

    @Test
    @DisplayName("""
            Given a question waiting for approval
            When its concept's owner sends PENDING as the decision
            Then a bad request is returned, as a review either accepts or declines
            """)
    void reviewQuestionReturnsBadRequestWhenStatusIsNotADecision() {
        // Given
        insertConcept("java-core", "Java Core", "Fundamentals", List.of(), ConceptStatus.ACTIVE, AUTHOR);
        Long questionId = insertQuestionAwaitingApproval(OTHER_USER, "java-core");
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = review(questionId, "java-core", "PENDING");

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("status must be ACCEPTED or DECLINED");
    }

    private RestTestClient.ResponseSpec review(Long questionId, String conceptTag, String status) {
        return restClient
                .put()
                .uri("/content/questions/{id}/concepts/{tag}/review", questionId, conceptTag)
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "status": "%s"
                        }
                        """.formatted(status))
                .exchange();
    }
}
