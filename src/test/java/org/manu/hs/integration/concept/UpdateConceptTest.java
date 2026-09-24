package org.manu.hs.integration.concept;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.api.content.dto.ConceptResponse;
import org.manu.hs.domain.concept.Concept;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.concept.ContributionPolicy;
import org.manu.hs.domain.user.UserRole;
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
        actingAs(ADMIN);

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
            Given the author of a draft concept
            When they send a PUT request with a valid payload
            Then the concept is updated with HTTP 200
            """)
    void updateConceptReturnsOkWhenAuthorEditsOwnDraftConcept() {
        // Given
        insertConcept("java-core", ConceptStatus.DRAFT);
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = update("java-core", """
                {
                    "name": "Java Core, reworded",
                    "description": "Clearer description"
                }
                """);

        // Then
        response.expectStatus().isOk();

        assertEquals("Java Core, reworded", findConceptByTag("java-core").getName());
    }

    @Test
    @DisplayName("""
            Given the owner of a concept that is already published
            When they send a PUT request with a valid payload
            Then the concept is updated with HTTP 200, as owners may keep improving their concepts
            """)
    void updateConceptReturnsOkWhenOwnerEditsPublishedConcept() {
        // Given
        insertConcept("java-core", ConceptStatus.ACTIVE);
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = update("java-core", """
                {
                    "name": "Java Core, reworded",
                    "description": "Clearer description"
                }
                """);

        // Then
        response.expectStatus().isOk();

        assertEquals("Java Core, reworded", findConceptByTag("java-core").getName());
        assertEquals(ConceptStatus.ACTIVE, findConceptByTag("java-core").getStatus());
    }

    @Test
    @DisplayName("""
            Given the owner of a concept that accepts contributed questions automatically
            When they send a PUT request setting the contribution policy to OWNER_APPROVAL
            Then the policy is changed and returned with HTTP 200
            """)
    void updateConceptChangesContributionPolicyWhenGiven() {
        // Given
        insertConcept("java-core", ConceptStatus.ACTIVE);
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = update("java-core", """
                {
                    "name": "Java Core",
                    "description": "Fundamentals",
                    "contributionPolicy": "OWNER_APPROVAL"
                }
                """);

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.contributionPolicy").isEqualTo("OWNER_APPROVAL");

        assertEquals(ContributionPolicy.OWNER_APPROVAL, findConceptByTag("java-core").getContributionPolicy());
    }

    @Test
    @DisplayName("""
            Given a concept whose owner approves contributed questions
            When a PUT request leaves the contribution policy out
            Then the concept keeps its policy
            """)
    void updateConceptKeepsContributionPolicyWhenOmitted() {
        // Given
        insertConcept("java-core", ConceptStatus.ACTIVE);
        setContributionPolicy("java-core", ContributionPolicy.OWNER_APPROVAL);
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = update("java-core", """
                {
                    "name": "Java Core, reworded",
                    "description": "Clearer description"
                }
                """);

        // Then
        response.expectStatus().isOk();

        assertEquals(ContributionPolicy.OWNER_APPROVAL, findConceptByTag("java-core").getContributionPolicy());
    }

    @Test
    @DisplayName("""
            Given a draft concept written by someone else, and a caller who is not an admin
            When they send a PUT request, even with an invalid payload
            Then a forbidden response is returned, before the payload is checked
            """)
    void updateConceptReturnsForbiddenWhenNonAuthorEditsDraftConcept() {
        // Given
        insertConcept("java-core", ConceptStatus.DRAFT);
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = update("java-core", """
                {
                    "description": "No name"
                }
                """);

        // Then
        response.expectStatus().isForbidden();
    }

    @Test
    @DisplayName("""
            Given the author of a draft concept and a payload missing a required field
            When a PUT request is sent to the concepts endpoint
            Then a bad request is returned and the existing concept is unchanged
            """)
    void updateConceptReturnsBadRequestWhenRequiredFieldIsMissing() {
        // Given
        insertConcept(
                "java-core",
                "Java Core",
                "Original description",
                List.of("original-reference"),
                ConceptStatus.DRAFT,
                AUTHOR);
        actingAs(AUTHOR);

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

    @Test
    @DisplayName("""
            Given a featured concept owned by a member, in the gym's pool
            When gym staff send a PUT request to the concepts endpoint for it
            Then the concept is updated and returned with HTTP 200, as staff curate the gym's pool
            """)
    void updateConceptReturnsOkWhenStaffEditsSomeoneElsesConcept() {
        // Given
        insertConcept("java-core", "Java Core", "Original description", List.of(), ConceptStatus.ACTIVE, AUTHOR);
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When
        RestTestClient.ResponseSpec response = update("java-core", """
                {
                    "name": "Java Core",
                    "description": "Fixed by staff"
                }
                """);

        // Then
        response.expectStatus().isOk();

        assertEquals("Fixed by staff", findConceptByTag("java-core").getDescription());
    }

    @Test
    @DisplayName("""
            Given a member's own concept, not featured
            When gym staff send a PUT request to the concepts endpoint for it
            Then HTTP 403 is returned and it is unchanged: a member's own concept is theirs to curate
            """)
    void updateConceptReturnsForbiddenWhenStaffEditsAMembersOwnConcept() {
        // Given
        insertCustomConcept("my-katas", ConceptStatus.ACTIVE, AUTHOR);
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When
        RestTestClient.ResponseSpec response = update("my-katas", """
                {
                    "name": "My katas",
                    "description": "Changed by staff"
                }
                """);

        // Then
        response.expectStatus().isForbidden();

        assertEquals("Description of my-katas", findConceptByTag("my-katas").getDescription());
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
