package org.manu.hs.integration.question;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.api.content.dto.QuestionResponse;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * GET /content/questions/{id}
 */
class GetQuestionTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a member's question on two concepts
            When its author sends a GET request for it
            Then the whole question is returned, model answer and concepts included
            """)
    void getQuestionReturnsTheQuestionToItsAuthor() {
        // Given
        insertConcept("java-core");
        insertConcept("java-generics");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core", "java-generics");
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = getQuestion(questionId);

        // Then
        QuestionResponse question = response
                .expectStatus().isOk()
                .expectBody(QuestionResponse.class)
                .returnResult()
                .getResponseBody();
        assertNotNull(question);
        assertEquals(questionId, question.getId());
        assertEquals("Original title", question.getTitle());
        assertNotNull(question.getSampleSolution());
        assertEquals(List.of("java-core", "java-generics"), question.getConceptTags());
    }

    @Test
    @DisplayName("""
            Given a member's question
            When a staff member sends a GET request for it
            Then the question is returned, as staff curate every question
            """)
    void getQuestionReturnsAnyQuestionToStaff() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When
        RestTestClient.ResponseSpec response = getQuestion(questionId);

        // Then
        response.expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(questionId.intValue());
    }

    @Test
    @DisplayName("""
            Given a member's question
            When another member sends a GET request for it
            Then forbidden is returned
            """)
    void getQuestionReturnsForbiddenToAnotherMember() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = getQuestion(questionId);

        // Then
        response.expectStatus().isForbidden();
    }

    @Test
    @DisplayName("""
            Given a member's question on another member's own concept
            When the concept's owner sends a GET request for it
            Then the whole question is returned, model answer included: they verify it, so they review it
            """)
    void getQuestionReturnsTheQuestionToWhoeverMayVerifyIt() {
        // Given
        insertCustomConcept("my-katas", ConceptStatus.ACTIVE, OTHER_USER);
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "my-katas");
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = getQuestion(questionId);

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(questionId.intValue())
                .jsonPath("$.sampleSolution").exists();
    }

    @Test
    @DisplayName("""
            Given no question with id 999
            When a signed-in member sends a GET request for it
            Then not found is returned
            """)
    void getQuestionReturnsNotFoundForAnUnknownId() {
        // Given
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = getQuestion(999L);

        // Then
        response.expectStatus().isNotFound();
    }

    @Test
    @DisplayName("""
            Given a member's question
            When a visitor who isn't signed in sends a GET request for it
            Then unauthorized is returned
            """)
    void getQuestionReturnsUnauthorizedForVisitors() {
        // Given
        insertConcept("java-core");
        Long questionId = insertQuestion(QuestionStatus.SUBMITTED, AUTHOR, "java-core");
        actingAsVisitor();

        // When
        RestTestClient.ResponseSpec response = getQuestion(questionId);

        // Then
        response.expectStatus().isUnauthorized();
    }

    private RestTestClient.ResponseSpec getQuestion(Long id) {
        return restClient
                .get()
                .uri("/content/questions/{id}", id)
                .exchange();
    }
}
