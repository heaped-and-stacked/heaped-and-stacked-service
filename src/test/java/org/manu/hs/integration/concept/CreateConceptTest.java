package org.manu.hs.integration.concept;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.api.content.dto.ConceptResponse;
import org.manu.hs.domain.concept.Concept;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * POST /content/concepts
 */
class CreateConceptTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a member and a valid payload for a concept with a tag that does not exist
            When a POST request is sent to the concepts endpoint
            Then the concept is created as a DRAFT owned by the member and returned with HTTP 201
            """)
    void createConceptReturnsCreatedWhenPayloadIsMinimal() {
        // Given
        actingAs(AUTHOR);
        String requestBody = """
                {
                    "tag": "java-core",
                    "name": "Java Core",
                    "description": "Fundamentals of Java programming language"
                }
                """;

        // When
        RestTestClient.ResponseSpec response = create(requestBody);

        // Then
        ConceptResponse createdConcept = response
                .expectStatus().isCreated()
                .expectBody(ConceptResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(createdConcept);
        assertEquals("java-core", createdConcept.getTag());
        assertEquals("Java Core", createdConcept.getName());
        assertEquals("Fundamentals of Java programming language", createdConcept.getDescription());
        assertEquals(ConceptStatus.DRAFT, createdConcept.getStatus());
        assertEquals(AUTHOR, createdConcept.getCreatedBy());
        assertEquals(AUTHOR, createdConcept.getUpdatedBy());
        assertNotNull(createdConcept.getCreatedAt());
        assertNotNull(createdConcept.getUpdatedAt());

        // Verify database entry
        Concept dbRecord = findConceptByTag("java-core");
        assertEquals("Java Core", dbRecord.getName());
        assertEquals(ConceptStatus.DRAFT, dbRecord.getStatus());
        assertEquals(AUTHOR, dbRecord.getCreatedBy());
    }

    @Test
    @DisplayName("""
            Given an admin and a valid payload for a new concept
            When a POST request is sent to the concepts endpoint
            Then the concept is created directly as ACTIVE
            """)
    void createConceptReturnsActiveConceptWhenCallerIsAdmin() {
        // Given
        actingAs(ADMIN);
        String requestBody = """
                {
                    "tag": "java-core",
                    "name": "Java Core",
                    "description": "Fundamentals of Java programming language"
                }
                """;

        // When
        RestTestClient.ResponseSpec response = create(requestBody);

        // Then
        ConceptResponse createdConcept = response
                .expectStatus().isCreated()
                .expectBody(ConceptResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(createdConcept);
        assertEquals(ConceptStatus.ACTIVE, createdConcept.getStatus());
        assertEquals(ConceptStatus.ACTIVE, findConceptByTag("java-core").getStatus());
    }

    @Test
    @DisplayName("""
            Given a valid payload for a concept with a tag that does not exist and external references
            When a POST request is sent to the concepts endpoint
            Then the concept is created with its references and returned with HTTP 201
            """)
    void createConceptReturnsCreatedWhenPayloadIsFull() {
        // Given
        String requestBody = """
                {
                    "tag": "java-core",
                    "name": "Java Core",
                    "description": "Fundamentals of Java programming language",
                    "externalReferences": [
                        "Effective Java — Joshua Bloch, 3rd Edition https://books.google.ro/books?id=ka2VUBqHiWkC&printsec=copyright&redir_esc=y#v=onepage&q&f=false",
                        "Java Brains — Java Essentials https://www.javabrains.io/courses/java-essentials"
                    ]
                }
                """;

        // When
        RestTestClient.ResponseSpec response = create(requestBody);

        // Then
        ConceptResponse createdConcept = response
                .expectStatus().isCreated()
                .expectBody(ConceptResponse.class)
                .returnResult()
                .getResponseBody();

        List<String> expectedReferences = List.of(
                "Effective Java — Joshua Bloch, 3rd Edition https://books.google.ro/books?id=ka2VUBqHiWkC&printsec=copyright&redir_esc=y#v=onepage&q&f=false",
                "Java Brains — Java Essentials https://www.javabrains.io/courses/java-essentials");

        assertNotNull(createdConcept);
        assertEquals(expectedReferences, createdConcept.getExternalReferences());

        // Verify database entry
        assertEquals(expectedReferences, findConceptByTag("java-core").getExternalReferences());
    }

    @Test
    @DisplayName("""
            Given a concept already exists for the given tag
            When a POST request is sent to the concepts endpoint
            Then a conflict is returned and the existing concept is unchanged
            """)
    void createConceptReturnsConflictWhenTagAlreadyExists() {
        // Given
        insertConcept(
                "java-core",
                "Java Core",
                "Original description",
                List.of("original-reference"));

        String requestBody = """
                {
                    "tag": "java-core",
                    "name": "Modified Java Core",
                    "description": "This should not replace the existing concept",
                    "externalReferences": [
                        "https://example.com/new-reference"
                    ]
                }
                """;

        // When
        RestTestClient.ResponseSpec response = create(requestBody);

        // Then
        response
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Concept with tag 'java-core' already exists");

        Concept existingConcept = findConceptByTag("java-core");
        assertEquals("Java Core", existingConcept.getName());
        assertEquals("Original description", existingConcept.getDescription());
        assertEquals(List.of("original-reference"), existingConcept.getExternalReferences());
    }

    @Test
    @DisplayName("""
            Given a payload missing a required field
            When a POST request is sent to the concepts endpoint
            Then a bad request is returned and nothing is persisted
            """)
    void createConceptReturnsBadRequestWhenRequiredFieldIsMissing() {
        // Given
        String requestBody = """
                {
                    "tag": "java-core",
                    "description": "Fundamentals of Java programming language"
                }
                """;

        // When
        RestTestClient.ResponseSpec response = create(requestBody);

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("name is required");

        assertConceptDoesNotExist("java-core");
    }

    @Test
    @DisplayName("""
            Given a payload with an invalid tag format
            When a POST request is sent to the concepts endpoint
            Then a bad request is returned and nothing is persisted
            """)
    void createConceptReturnsBadRequestWhenTagIsInvalid() {
        // Given
        String requestBody = """
                {
                    "tag": "Java Core!",
                    "name": "Java Core",
                    "description": "Fundamentals of Java programming language"
                }
                """;

        // When
        RestTestClient.ResponseSpec response = create(requestBody);

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Concept tag 'Java Core!' must be kebab-case");

        assertConceptDoesNotExist("Java Core!");
    }

    private RestTestClient.ResponseSpec create(String requestBody) {
        return restClient
                .post()
                .uri("/content/concepts")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange();
    }
}
