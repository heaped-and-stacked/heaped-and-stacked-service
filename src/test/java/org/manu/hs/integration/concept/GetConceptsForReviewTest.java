package org.manu.hs.integration.concept;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.manu.hs.api.content.dto.ConceptResponse;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * GET /content/concepts/review
 */
class GetConceptsForReviewTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given the caller is an admin and no status filter is provided
            When a GET request is sent to the concepts review endpoint
            Then only DRAFT concepts are returned, oldest first
            """)
    void getConceptsForReviewReturnsDraftsOldestFirstWhenNoFilterProvided() {
        // Given
        insertConcept("java-streams", ConceptStatus.DRAFT);
        insertConcept("java-core", ConceptStatus.ACTIVE);
        insertConcept("java-generics", ConceptStatus.DRAFT);
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = getForReview();

        // Then
        List<ConceptResponse> concepts = response
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<ConceptResponse>>() {
                })
                .returnResult()
                .getResponseBody();

        assertNotNull(concepts);
        assertEquals(List.of("java-streams", "java-generics"), concepts.stream().map(ConceptResponse::getTag).toList());
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(ConceptStatus.class)
    @DisplayName("""
            Given the caller is an admin and a status filter is provided
            When a GET request is sent to the concepts review endpoint
            Then only concepts in that status are returned
            """)
    void getConceptsForReviewReturnsFilteredConceptsWhenStatusProvided(ConceptStatus filter) {
        // Given
        for (ConceptStatus status : ConceptStatus.values()) {
            insertConcept("concept-" + status.name().toLowerCase(), status);
        }
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = getForReview(filter.name());

        // Then
        List<ConceptResponse> concepts = response
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<ConceptResponse>>() {
                })
                .returnResult()
                .getResponseBody();

        assertNotNull(concepts);
        assertEquals(List.of(filter), concepts.stream().map(ConceptResponse::getStatus).toList());
    }

    @Test
    @DisplayName("""
            Given the caller is not an admin
            When a GET request is sent to the concepts review endpoint
            Then a forbidden response is returned
            """)
    void getConceptsForReviewReturnsForbiddenWhenCallerIsNotAdmin() {
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
            When a GET request is sent to the concepts review endpoint
            Then a bad request is returned with a message
            """)
    void getConceptsForReviewReturnsBadRequestWhenStatusIsUnknown() {
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
                .uri("/content/concepts/review")
                .exchange();
    }

    private RestTestClient.ResponseSpec getForReview(String status) {
        return restClient
                .get()
                .uri("/content/concepts/review?status={status}", status)
                .exchange();
    }
}
