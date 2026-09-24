package org.manu.hs.integration.question;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.manu.hs.api.content.dto.QuestionResponse;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * GET /content/questions/review
 */
class GetQuestionsForReviewTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given the caller is an admin and no status filter is provided
            When a GET request is sent to the questions review endpoint
            Then only DRAFT questions are returned, oldest first
            """)
    void getQuestionsForReviewReturnsDraftsOldestFirstWhenNoFilterProvided() {
        // Given
        insertConcept("java-core");
        Long olderDraft = insertQuestion(QuestionStatus.DRAFT, AUTHOR, "java-core");
        insertQuestion(QuestionStatus.ACTIVE, AUTHOR, "java-core");
        Long newerDraft = insertQuestion(QuestionStatus.DRAFT, OTHER_USER, "java-core");
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = getForReview();

        // Then
        List<QuestionResponse> questions = response
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<QuestionResponse>>() {
                })
                .returnResult()
                .getResponseBody();

        assertNotNull(questions);
        assertEquals(List.of(olderDraft, newerDraft), questions.stream().map(QuestionResponse::getId).toList());
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(QuestionStatus.class)
    @DisplayName("""
            Given the caller is an admin and a status filter is provided
            When a GET request is sent to the questions review endpoint
            Then only questions in that status are returned
            """)
    void getQuestionsForReviewReturnsFilteredQuestionsWhenStatusProvided(QuestionStatus filter) {
        // Given
        insertConcept("java-core");
        for (QuestionStatus status : QuestionStatus.values()) {
            insertQuestion(status, AUTHOR, "java-core");
        }
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = getForReview(filter.name());

        // Then
        List<QuestionResponse> questions = response
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<QuestionResponse>>() {
                })
                .returnResult()
                .getResponseBody();

        assertNotNull(questions);
        assertEquals(List.of(filter), questions.stream().map(QuestionResponse::getStatus).toList());
    }

    @Test
    @DisplayName("""
            Given the caller is not an admin
            When a GET request is sent to the questions review endpoint
            Then a forbidden response is returned
            """)
    void getQuestionsForReviewReturnsForbiddenWhenCallerIsNotAdmin() {
        // Given
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = getForReview();

        // Then
        response.expectStatus().isForbidden();
    }

    @Test
    @DisplayName("""
            Given the caller is an admin and the status filter does not exist
            When a GET request is sent to the questions review endpoint
            Then a bad request is returned with a message
            """)
    void getQuestionsForReviewReturnsBadRequestWhenStatusIsUnknown() {
        // Given
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = getForReview("LIVE");

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isNotEmpty();
    }

    private RestTestClient.ResponseSpec getForReview() {
        return restClient
                .get()
                .uri("/content/questions/review")
                .exchange();
    }

    private RestTestClient.ResponseSpec getForReview(String status) {
        return restClient
                .get()
                .uri("/content/questions/review?status={status}", status)
                .exchange();
    }
}
