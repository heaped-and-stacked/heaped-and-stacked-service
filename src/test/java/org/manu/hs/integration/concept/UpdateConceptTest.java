package org.manu.hs.integration.concept;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.api.content.dto.ConceptResponse;
import org.manu.hs.domain.concept.Concept;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * PUT /content/concepts/{tag}
 */
class UpdateConceptTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a concept exists for the given tag
            When a PUT request is sent to the concepts endpoint with a valid payload
            Then the concept is updated, its status is kept and it is returned with HTTP 200
            """)
    void updateConceptReturnsOkWhenPayloadIsValid() {
        // Given
        insertConcept(
                "java-core",
                "Java Core",
                "Original description",
                List.of("original-reference"),
                ConceptStatus.ACTIVE,
                AUTHOR);
        actingAs(ADMIN, true);

        String requestBody = """
                {
                    "name": "Advanced Java Core",
                    "description": "Updated Java programming fundamentals",
                    "externalReferences": [
                        "updated-reference-1",
                        "updated-reference-2"
                    ]
                }
                """;

        // When
        RestTestClient.ResponseSpec response = update("java-core", requestBody);

        // Then
        ConceptResponse updatedConcept = response
                .expectStatus().isOk()
                .expectBody(ConceptResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(updatedConcept);
        assertEquals("java-core", updatedConcept.getTag());
        assertEquals("Advanced Java Core", updatedConcept.getName());
        assertEquals("Updated Java programming fundamentals", updatedConcept.getDescription());
        assertEquals(List.of("updated-reference-1", "updated-reference-2"), updatedConcept.getExternalReferences());
        assertEquals(ConceptStatus.ACTIVE, updatedConcept.getStatus());
        assertEquals(AUTHOR, updatedConcept.getCreatedBy());
        assertEquals(ADMIN, updatedConcept.getUpdatedBy());

        // Verify database entry
        Concept databaseConcept = findConceptByTag("java-core");
        assertEquals("Advanced Java Core", databaseConcept.getName());
        assertEquals(ConceptStatus.ACTIVE, databaseConcept.getStatus());
        assertEquals(AUTHOR, databaseConcept.getCreatedBy());
        assertEquals(ADMIN, databaseConcept.getUpdatedBy());
    }

    @Test
    @DisplayName("""
            Given no concept exists for the given tag
            When a PUT request is sent to the concepts endpoint
            Then a not found response is returned
            """)
    void updateConceptReturnsNotFoundWhenConceptDoesNotExist() {
        // Given
        String requestBody = """
                {
                    "name": "Ghost Concept",
                    "description": "This concept was never created",
                    "externalReferences": []
                }
                """;

        // When
        RestTestClient.ResponseSpec response = update("does-not-exist", requestBody);

        // Then
        response
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Concept with tag 'does-not-exist' not found");
    }

    @Test
    @DisplayName("""
            Given no concept exists for the given tag and the payload is also invalid
            When a PUT request is sent to the concepts endpoint
            Then not found is reported before the invalid payload
            """)
    void updateConceptReturnsNotFoundWhenConceptDoesNotExistAndPayloadIsInvalid() {
        // Given
        String requestBody = """
                {
                    "description": "No name, and no concept either"
                }
                """;

        // When
        RestTestClient.ResponseSpec response = update("does-not-exist", requestBody);

        // Then
        response.expectStatus().isNotFound();
    }

    @Test
    @DisplayName("""
            Given a payload missing a required field
            When a PUT request is sent to the concepts endpoint
            Then a bad request is returned and the existing concept is unchanged
            """)
    void updateConceptReturnsBadRequestWhenRequiredFieldIsMissing() {
        // Given
        insertConcept(
                "java-core",
                "Java Core",
                "Original description",
                List.of("original-reference"));

        String requestBody = """
                {
                    "description": "Updated description without a name"
                }
                """;

        // When
        RestTestClient.ResponseSpec response = update("java-core", requestBody);

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("name is required");

        assertEquals("Original description", findConceptByTag("java-core").getDescription());
    }

    private RestTestClient.ResponseSpec update(String tag, String requestBody) {
        return restClient
                .put()
                .uri("/content/concepts/{tag}", tag)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange();
    }
}
