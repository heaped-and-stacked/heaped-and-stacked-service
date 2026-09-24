package org.manu.hs.integration.journey;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.api.content.dto.QuestionPageResponse;
import org.manu.hs.api.content.dto.QuestionResponse;
import org.manu.hs.domain.user.AuthProvider;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Journey: a visitor signs in with GitHub for the first time and contributes a question.
 * <p>
 * Journeys cross several endpoints to check that the pieces work together. The single
 * endpoint rules are covered by the endpoint test classes.
 */
class ContributorJourneyTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a visitor with a GitHub account and an approved concept
            When they sign in for the first time, check their profile and submit a question
            Then they are a new member, and the question is submitted and listed as theirs
            """)
    void firstSignInThenContributeAQuestion() {
        // Given
        insertConcept("java-core");

        // When: they sign in with GitHub for the first time
        RestTestClient browser = signInWithGitHub("5001", "new-coder");

        // Then: they are registered as a member, named after their GitHub username
        browser.get().uri("/users/me").exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.role").isEqualTo("MEMBER")
                .jsonPath("$.displayName").isEqualTo("new-coder");

        // When: they submit a question
        QuestionResponse submitted = browser.post().uri("/content/questions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(validQuestionJson("java-core"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(QuestionResponse.class)
                .returnResult()
                .getResponseBody();

        // Then: it is submitted, owned by them, and listed in their Coach's Corner
        assertNotNull(submitted);
        assertEquals("SUBMITTED", submitted.getStatus().name());
        assertEquals(findIdentity(AuthProvider.GITHUB, "5001").getUserId(), submitted.getCreatedBy());

        QuestionPageResponse mine = browser.get().uri("/content/questions").exchange()
                .expectStatus().isOk()
                .expectBody(QuestionPageResponse.class)
                .returnResult()
                .getResponseBody();
        assertNotNull(mine);
        assertEquals(List.of(submitted.getId()), mine.getItems().stream().map(QuestionResponse::getId).toList());
    }
}
