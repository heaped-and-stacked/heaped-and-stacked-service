package org.manu.hs.integration.concept;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.api.content.dto.ConceptResponse;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.user.UserRole;
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
            Given concepts exist in the system, one of them the caller's draft
            When a GET request is sent to the concepts endpoint
            Then all of them are returned ordered by tag, with their status, with HTTP 200
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
            Given a gym concept and a member's own concept, both published
            When a GET request is sent to the concepts endpoint
            Then both are returned, the gym's marked featured (in the gym's pool) and the member's
            not (a custom concept, in the community list)
            """)
    void getConceptsMarksFeaturedConcepts() {
        // Given
        insertConcept("java-core", ConceptStatus.ACTIVE);
        insertCustomConcept("my-katas", ConceptStatus.ACTIVE, OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = getConcepts();

        // Then
        response.expectStatus().isOk().expectBody()
                .jsonPath("$[0].tag").isEqualTo("java-core")
                .jsonPath("$[0].featured").isEqualTo(true)
                .jsonPath("$[1].tag").isEqualTo("my-katas")
                .jsonPath("$[1].featured").isEqualTo(false);
    }

    @Test
    @DisplayName("""
            Given one of the gym's own concepts (owned by the house account) and a member's concept
            that staff featured
            When a GET request is sent to the concepts endpoint
            Then both are featured, and only the first is marked as the gym's own
            """)
    void getConceptsMarksTheGymsOwnConcepts() {
        // Given
        Long house = insertHouseAccount();
        insertConcept("java-core", "Java Core", "Fundamentals", List.of(), ConceptStatus.ACTIVE, house);
        insertConcept("my-katas", "My katas", "Katas", List.of(), ConceptStatus.ACTIVE, OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = getConcepts();

        // Then
        response.expectStatus().isOk().expectBody()
                .jsonPath("$[0].tag").isEqualTo("java-core")
                .jsonPath("$[0].houseOwned").isEqualTo(true)
                .jsonPath("$[1].tag").isEqualTo("my-katas")
                .jsonPath("$[1].featured").isEqualTo(true)
                .jsonPath("$[1].houseOwned").isEqualTo(false);
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

    @Test
    @DisplayName("""
            Given a published and a retired concept, and a draft owned by someone else
            When a visitor who is not signed in sends a GET request to the concepts endpoint
            Then only the published and retired concepts are returned: drafts stay private
            """)
    void getConceptsHidesDraftsFromVisitors() {
        // Given
        insertConcept("spring-boot", ConceptStatus.ACTIVE);
        insertConcept("java-applets", ConceptStatus.RETIRED);
        insertConcept("java-core", ConceptStatus.DRAFT);
        actingAsVisitor();

        // When
        RestTestClient.ResponseSpec response = getConcepts();

        // Then
        assertEquals(List.of("java-applets", "spring-boot"), tagsOf(response));
    }

    @Test
    @DisplayName("""
            Given draft concepts owned by the caller and by someone else
            When a member sends a GET request to the concepts endpoint
            Then their own draft is returned, someone else's isn't
            """)
    void getConceptsReturnsOnlyOwnDraftsToMembers() {
        // Given
        insertConcept("java-core", "Java Core", "Fundamentals", List.of(), ConceptStatus.DRAFT, AUTHOR);
        insertConcept("java-generics", "Java Generics", "Generics", List.of(), ConceptStatus.DRAFT, OTHER_USER);
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = getConcepts();

        // Then
        assertEquals(List.of("java-generics"), tagsOf(response));
    }

    @Test
    @DisplayName("""
            Given a draft concept owned by a member
            When gym staff send a GET request to the concepts endpoint
            Then the draft is left out: a member's draft is private to them, staff included
            """)
    void getConceptsKeepsMembersDraftsFromStaff() {
        // Given
        insertConcept("java-core", ConceptStatus.DRAFT);
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When
        RestTestClient.ResponseSpec response = getConcepts();

        // Then
        assertEquals(List.of(), tagsOf(response));
    }

    private static List<String> tagsOf(RestTestClient.ResponseSpec response) {
        List<ConceptResponse> concepts = response
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<ConceptResponse>>() {
                })
                .returnResult()
                .getResponseBody();
        assertNotNull(concepts);
        return concepts.stream().map(ConceptResponse::getTag).toList();
    }

    private RestTestClient.ResponseSpec getConcepts() {
        return restClient
                .get()
                .uri("/content/concepts")
                .exchange();
    }
}
