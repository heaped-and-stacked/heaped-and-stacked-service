package org.manu.hs.integration.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.manu.hs.api.user.dto.CurrentUserResponse;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.domain.user.UserStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * GET /users/me
 */
class GetCurrentUserTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a signed-in member
            When a GET request is sent to the current user endpoint
            Then their own profile is returned with HTTP 200
            """)
    void getCurrentUserReturnsProfileWhenSignedIn() {
        // Given
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = getCurrentUser();

        // Then
        CurrentUserResponse user = response
                .expectStatus().isOk()
                .expectBody(CurrentUserResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(user);
        assertEquals(AUTHOR, user.getId());
        assertEquals("User " + AUTHOR, user.getDisplayName());
        assertEquals("https://avatars.example.com/" + AUTHOR, user.getAvatarUrl());
        assertEquals(UserRole.MEMBER, user.getRole());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
        assertNotNull(user.getCreatedAt());
    }

    @Test
    @DisplayName("""
            Given a signed-in admin
            When a GET request is sent to the current user endpoint
            Then their profile shows the ADMIN role, which the UI uses to show admin features
            """)
    void getCurrentUserReturnsAdminRoleWhenCallerIsAdmin() {
        // Given
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = getCurrentUser();

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(ADMIN.intValue())
                .jsonPath("$.role").isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("""
            Given a visitor who is not signed in
            When a GET request is sent to the current user endpoint
            Then an unauthorized response is returned, which the UI uses to offer signing in
            """)
    void getCurrentUserReturnsUnauthorizedWhenVisitor() {
        // Given
        actingAsVisitor();

        // When
        RestTestClient.ResponseSpec response = getCurrentUser();

        // Then
        response
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Sign in required");
    }

    private RestTestClient.ResponseSpec getCurrentUser() {
        return restClient
                .get()
                .uri("/users/me")
                .exchange();
    }

    @Test
    @DisplayName("""
            Given contributions are open to members (the setting is on)
            When a signed-in member asks for their profile
            Then it says they can contribute, so the web app offers ADD buttons
            """)
    void getCurrentUserSaysAMemberCanContributeWhenOpen() {
        // Given
        actingAs(AUTHOR);

        // When / Then
        getCurrentUser()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.canContribute").isEqualTo(true);
    }

    @Nested
    @TestPropertySource(properties = "app.content.members-contribute=false")
    class WhenOnlyStaffContribute {

        @Test
        @DisplayName("""
                Given contributions are open to staff only
                When a member and then staff ask for their profile
                Then the member can't contribute and staff can
                """)
        void getCurrentUserSaysOnlyStaffCanContribute() {
            // Given
            insertUser(STAFF, UserRole.STAFF);
            actingAs(AUTHOR);

            // When / Then
            getCurrentUser().expectStatus().isOk().expectBody().jsonPath("$.canContribute").isEqualTo(false);

            actingAs(STAFF);
            getCurrentUser().expectStatus().isOk().expectBody().jsonPath("$.canContribute").isEqualTo(true);
        }
    }
}
