package org.manu.hs.integration.concept;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PUT /content/concepts/{tag}/featured: staff take a member's concept into the gym's pool
 * ("all muscles"), or take it out again. The owner stays its owner either way.
 */
class FeatureConceptTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a member's own published concept
            When a staff member features it
            Then HTTP 200 is returned with the concept featured, and its owner is still the member
            """)
    void featureConceptReturnsOkWhenStaffFeatures() {
        // Given
        insertCustomConcept("my-katas", ConceptStatus.ACTIVE, OTHER_USER);
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When
        RestTestClient.ResponseSpec response = feature("my-katas", true);

        // Then
        response.expectStatus().isOk().expectBody()
                .jsonPath("$.featured").isEqualTo(true)
                .jsonPath("$.createdBy").isEqualTo(OTHER_USER);

        assertTrue(findConceptByTag("my-katas").isFeatured());
        assertEquals(OTHER_USER, findConceptByTag("my-katas").getCreatedBy());
    }

    @Test
    @DisplayName("""
            Given a featured concept
            When a staff member unfeatures it
            Then HTTP 200 is returned and the concept is back to its owner's own, out of the gym's pool
            """)
    void featureConceptReturnsOkWhenStaffUnfeatures() {
        // Given
        insertConcept("java-core", ConceptStatus.ACTIVE);
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When
        RestTestClient.ResponseSpec response = feature("java-core", false);

        // Then
        response.expectStatus().isOk().expectBody().jsonPath("$.featured").isEqualTo(false);
        assertFalse(findConceptByTag("java-core").isFeatured());
    }

    @Test
    @DisplayName("""
            Given a member's own published concept
            When the member who owns it tries to feature it
            Then HTTP 403 is returned and it stays unfeatured: what joins the gym's pool is for staff
            """)
    void featureConceptReturnsForbiddenForMembers() {
        // Given
        insertCustomConcept("my-katas", ConceptStatus.ACTIVE, OTHER_USER);
        actingAs(OTHER_USER);

        // When
        RestTestClient.ResponseSpec response = feature("my-katas", true);

        // Then
        response.expectStatus().isForbidden();
        assertFalse(findConceptByTag("my-katas").isFeatured());
    }

    @Test
    @DisplayName("""
            Given a member's draft concept and a retired one
            When a staff member tries to feature each
            Then HTTP 409 is returned for both: only a published concept can join the gym's pool
            """)
    void featureConceptReturnsConflictWhenNotPublished() {
        // Given
        insertCustomConcept("my-draft", ConceptStatus.DRAFT, OTHER_USER);
        insertCustomConcept("my-old", ConceptStatus.RETIRED, OTHER_USER);
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When / Then
        feature("my-draft", true).expectStatus().isEqualTo(409);
        feature("my-old", true).expectStatus().isEqualTo(409);
    }

    @Test
    @DisplayName("""
            Given no concept with the tag "missing", and a concept
            When a staff member features "missing", and then the concept without saying whether
            Then HTTP 404 is returned for the first and HTTP 400 for the second
            """)
    void featureConceptReturnsNotFoundAndBadRequest() {
        // Given
        insertCustomConcept("my-katas", ConceptStatus.ACTIVE, OTHER_USER);
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When / Then
        feature("missing", true).expectStatus().isNotFound();
        restClient.put()
                .uri("/content/concepts/{tag}/featured", "my-katas")
                .contentType(MediaType.APPLICATION_JSON)
                .body("{}")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("""
            Given one of the gym's own concepts, owned by the house account
            When a staff member tries to unfeature it
            Then HTTP 409 is returned and it stays featured: nobody can sign in as the house account,
            so out of the gym's pool nobody would curate it
            """)
    void featureConceptReturnsConflictWhenUnfeaturingTheGymsOwn() {
        // Given
        Long house = insertHouseAccount();
        insertConcept("java-core", "Java Core", "Fundamentals", java.util.List.of(), ConceptStatus.ACTIVE, house);
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When
        RestTestClient.ResponseSpec response = feature("java-core", false);

        // Then
        response.expectStatus().isEqualTo(409);
        assertTrue(findConceptByTag("java-core").isFeatured());
    }

    private RestTestClient.ResponseSpec feature(String tag, boolean featured) {
        return restClient
                .put()
                .uri("/content/concepts/{tag}/featured", tag)
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "featured": %s
                        }
                        """.formatted(featured))
                .exchange();
    }
}
