package org.manu.hs.integration.concept;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.api.content.dto.ConceptResponse;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * GET /content/concepts
 */
class GetConceptsTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given concepts exist in the system
            When a GET request is sent to the concepts endpoint
            Then all concepts are returned ordered by tag, with their status, with HTTP 200
            """)
    void getConceptsReturnsAllConceptsWhenConceptsExist() {
        // Given
        insertConcept("spring-boot", ConceptStatus.ACTIVE);
        insertConcept("java-core", ConceptStatus.DRAFT);

        // When
        RestTestClient.ResponseSpec response = getConcepts();

        // Then
        List<ConceptResponse> concepts = response
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<ConceptResponse>>() {
                })
                .returnResult()
                .getResponseBody();

        assertNotNull(concepts);
        assertEquals(List.of("java-core", "spring-boot"), concepts.stream().map(ConceptResponse::getTag).toList());
        assertEquals(ConceptStatus.DRAFT, concepts.get(0).getStatus());
        assertEquals(ConceptStatus.ACTIVE, concepts.get(1).getStatus());
        assertEquals(AUTHOR, concepts.get(0).getCreatedBy());
    }

    @Test
    @DisplayName("""
            Given no concepts exist in the system
            When a GET request is sent to the concepts endpoint
            Then an empty list is returned with HTTP 200
            """)
    void getConceptsReturnsEmptyListWhenNoConceptsExist() {
        // When
        RestTestClient.ResponseSpec response = getConcepts();

        // Then
        List<ConceptResponse> concepts = response
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<ConceptResponse>>() {
                })
                .returnResult()
                .getResponseBody();

        assertNotNull(concepts);
        assertTrue(concepts.isEmpty());
    }

    private RestTestClient.ResponseSpec getConcepts() {
        return restClient
                .get()
                .uri("/content/concepts")
                .exchange();
    }
}
