package org.manu.hs.integration.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.question.ConceptLinkStatus;
import org.manu.hs.domain.user.AuthProvider;
import org.manu.hs.domain.user.User;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.domain.user.UserStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * DELETE /users/me
 */
@ExtendWith(OutputCaptureExtension.class)
class DeleteCurrentUserTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a signed-in member with a GitHub login
            When a DELETE request is sent to the current user endpoint
            Then the account is soft deleted, personal data and logins are removed, and HTTP 204 is returned
            """)
    void deleteCurrentUserReturnsNoContentAndSoftDeletesTheAccount() {
        // Given
        insertIdentity(AUTHOR, AuthProvider.GITHUB, "5001", "author-on-github");
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = deleteCurrentUser();

        // Then
        response.expectStatus().isNoContent();

        // Verify database entry: the row stays (content keeps its author), personal data goes
        User deleted = findUser(AUTHOR);
        assertEquals(UserStatus.DELETED, deleted.getStatus());
        assertNotNull(deleted.getDeletedAt());
        assertNull(deleted.getDisplayName());
        assertNull(deleted.getAvatarUrl());
        assertEquals(0, countIdentitiesOf(AUTHOR));
    }

    @Test
    @DisplayName("""
            Given a signed-in member who deletes their account
            When they send another request with the same session
            Then they are treated as a visitor, because deleting the account ended the session
            """)
    void deleteCurrentUserEndsTheSession() {
        // Given
        actingAs(AUTHOR);
        deleteCurrentUser().expectStatus().isNoContent();

        // When
        RestTestClient.ResponseSpec response = restClient
                .get()
                .uri("/users/me")
                .exchange();

        // Then
        response.expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("""
            Given a member signed in on two devices
            When they delete their account from one of them
            Then the other device is signed out too
            """)
    void deleteCurrentUserEndsSessionsOnOtherDevicesToo() {
        // Given
        RestTestClient otherDevice = clientSignedInAs(AUTHOR);
        actingAs(AUTHOR);

        // When
        deleteCurrentUser().expectStatus().isNoContent();

        // Then
        otherDevice
                .get()
                .uri("/users/me")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("""
            Given the signed-in user is the last active admin
            When a DELETE request is sent to the current user endpoint
            Then a conflict is returned and the account is kept, so the platform always has an admin
            """)
    void deleteCurrentUserReturnsConflictWhenLastActiveAdmin() {
        // Given
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = deleteCurrentUser();

        // Then
        response
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("User '" + ADMIN + "' is the last active admin and can't be demoted, blocked or deleted");

        assertEquals(UserStatus.ACTIVE, findUser(ADMIN).getStatus());
    }

    @Test
    @DisplayName("""
            Given a signed-in admin while another active admin exists
            When a DELETE request is sent to the current user endpoint
            Then the account is deleted with HTTP 204
            """)
    void deleteCurrentUserReturnsNoContentWhenAnotherAdminRemains() {
        // Given
        insertUser(1004L, UserRole.ADMIN);
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = deleteCurrentUser();

        // Then
        response.expectStatus().isNoContent();

        assertEquals(UserStatus.DELETED, findUser(ADMIN).getStatus());
    }

    @Test
    @DisplayName("""
            Given a visitor who is not signed in
            When a DELETE request is sent to the current user endpoint
            Then an unauthorized response is returned
            """)
    void deleteCurrentUserReturnsUnauthorizedWhenVisitor() {
        // Given
        actingAsVisitor();

        // When
        RestTestClient.ResponseSpec response = deleteCurrentUser();

        // Then
        response.expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("""
            Given a signed-in member
            When they delete their account
            Then the audit log records it, by id only
            """)
    void accountDeletionIsAudited(CapturedOutput output) {
        // When
        deleteCurrentUser().expectStatus().isNoContent();

        // Then
        assertTrue(output.getAll().contains("account_deleted user=1001"));
    }

    @Test
    @DisplayName("""
            Given a member who owns a concept with an exercise waiting on it
            When they delete their account
            Then the concept passes to the house account, and the exercise keeps waiting
            """)
    void deletingAnAccountHandsItsConceptsToTheHouseAccount() {
        // Given
        Long house = insertHouseAccount();
        insertCustomConcept("my-katas", ConceptStatus.ACTIVE, AUTHOR);
        Long waiting = insertQuestionAwaitingApproval(OTHER_USER, "my-katas");
        actingAs(AUTHOR);

        // When
        deleteCurrentUser().expectStatus().isNoContent();

        // Then
        assertEquals(house, findConceptByTag("my-katas").getCreatedBy());
        assertEquals(ConceptLinkStatus.PENDING, findConceptLinkStatus(waiting, "my-katas"));
    }

    private RestTestClient.ResponseSpec deleteCurrentUser() {
        return restClient
                .delete()
                .uri("/users/me")
                .exchange();
    }
}
