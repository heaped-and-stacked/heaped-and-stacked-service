package org.manu.hs.integration.concept;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.manu.hs.api.content.dto.ConceptResponse;
import org.manu.hs.domain.concept.Concept;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.concept.ContributionPolicy;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * POST /content/concepts
 */
class CreateConceptTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a member and a valid payload for a concept with a tag that does not exist
            When a POST request is sent to the concepts endpoint
            Then the concept is created published (ACTIVE), owned by the member, and returned with HTTP 201
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
        assertEquals(ConceptStatus.ACTIVE, createdConcept.getStatus());
        assertEquals(ContributionPolicy.AUTO_ACCEPT, createdConcept.getContributionPolicy());
        assertEquals(AUTHOR, createdConcept.getCreatedBy());
        assertEquals(AUTHOR, createdConcept.getUpdatedBy());
        assertNotNull(createdConcept.getCreatedAt());
        assertNotNull(createdConcept.getUpdatedAt());

        // Verify database entry
        Concept dbRecord = findConceptByTag("java-core");
        assertEquals("Java Core", dbRecord.getName());
        assertEquals(ConceptStatus.ACTIVE, dbRecord.getStatus());
        assertEquals(ContributionPolicy.AUTO_ACCEPT, dbRecord.getContributionPolicy());
        assertEquals(AUTHOR, dbRecord.getCreatedBy());
    }

    @Test
    @DisplayName("""
            Given a valid payload for a new concept with external references and a contribution policy
            When a POST request is sent to the concepts endpoint
            Then the concept is created with its references and policy, and returned with HTTP 201
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
                    ],
                    "contributionPolicy": "OWNER_APPROVAL"
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
        assertEquals(ContributionPolicy.OWNER_APPROVAL, createdConcept.getContributionPolicy());

        // Verify database entry
        Concept dbRecord = findConceptByTag("java-core");
        assertEquals(expectedReferences, dbRecord.getExternalReferences());
        assertEquals(ContributionPolicy.OWNER_APPROVAL, dbRecord.getContributionPolicy());
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

    @Test
    @DisplayName("""
            Given a member, and gym staff
            When each creates a concept
            Then both are published (ACTIVE) straight away as their author's own, not featured:
            the gym's pool only grows when staff feature a concept
            """)
    void createConceptIsPublishedAsTheAuthorsOwn() {
        // Given
        insertUser(STAFF, UserRole.STAFF);

        // When
        actingAs(AUTHOR);
        create(MINIMAL_CONCEPT).expectStatus().isCreated();
        actingAs(STAFF);
        create(MINIMAL_CONCEPT.replace("java-core", "java-generics")).expectStatus().isCreated();

        // Then
        assertEquals(ConceptStatus.ACTIVE, findConceptByTag("java-core").getStatus());
        assertFalse(findConceptByTag("java-core").isFeatured());
        assertEquals(ConceptStatus.ACTIVE, findConceptByTag("java-generics").getStatus());
        assertFalse(findConceptByTag("java-generics").isFeatured());
    }

    private static final String MINIMAL_CONCEPT = """
            {
                "tag": "java-core",
                "name": "Java Core",
                "description": "Fundamentals"
            }
            """;

    private RestTestClient.ResponseSpec create(String requestBody) {
        return restClient
                .post()
                .uri("/content/concepts")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange();
    }

    @Nested
    @TestPropertySource(properties = "app.content.members-add-concepts=false")
    class WhenOnlyStaffAddConcepts {

        @Test
        @DisplayName("""
                Given only staff may add concepts
                When a member creates a concept
                Then a forbidden response is returned and nothing is stored
                """)
        void createConceptReturnsForbiddenForAMember() {
            // Given
            actingAs(AUTHOR);

            // When
            RestTestClient.ResponseSpec response = create(MINIMAL_CONCEPT);

            // Then
            response
                    .expectStatus().isForbidden()
                    .expectBody()
                    .jsonPath("$.message").isEqualTo("Only staff may add concepts for now");

            assertEquals(0, countConcepts());
        }

        @Test
        @DisplayName("""
                Given only staff may add concepts
                When staff create a concept
                Then it is created
                """)
        void createConceptStillWorksForStaff() {
            // Given
            insertUser(STAFF, UserRole.STAFF);
            actingAs(STAFF);

            // When / Then
            create(MINIMAL_CONCEPT).expectStatus().isCreated();
        }
    }
}
