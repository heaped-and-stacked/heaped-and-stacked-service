package org.manu.hs.integration.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.domain.user.UserStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PUT /admin/users/{id}/role
 */
@ExtendWith(OutputCaptureExtension.class)
class UpdateUserRoleTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a signed-in admin
            When they try to make a member ADMIN
            Then a bad request is returned and the member stays a member: admins come only from
            the configuration (BOOTSTRAP_ADMIN_IDS), never from the app
            """)
    void updateUserRoleReturnsBadRequestWhenPromotingToAdmin() {
        // Given
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = updateRole(AUTHOR, "ADMIN");

        // Then
        response.expectStatus().isBadRequest();

        assertEquals(UserRole.MEMBER, findUser(AUTHOR).getRole());
    }

    @Test
    @DisplayName("""
            Given a signed-in admin and another admin
            When they try to demote the other admin to MEMBER
            Then a conflict is returned and the other admin stays ADMIN: like granting it, taking it
            away is done in the configuration
            """)
    void updateUserRoleReturnsConflictWhenDemotingAnotherAdmin() {
        // Given
        insertUser(1004L, UserRole.ADMIN);
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = updateRole(1004L, "MEMBER");

        // Then
        response.expectStatus().isEqualTo(HttpStatus.CONFLICT);

        assertEquals(UserRole.ADMIN, findUser(1004L).getRole());
    }

    @Test
    @DisplayName("""
            Given a member who is signed in elsewhere
            When an admin changes their role
            Then that other session is signed out, so the new role applies from their next sign-in
            """)
    void updateUserRoleEndsTheUsersSessions() {
        // Given
        RestTestClient membersBrowser = clientSignedInAs(AUTHOR);
        actingAs(ADMIN);

        // When
        updateRole(AUTHOR, "STAFF").expectStatus().isOk();

        // Then
        membersBrowser
                .get()
                .uri("/users/me")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("""
            Given the signed-in admin is the last active admin
            When they demote themselves to MEMBER
            Then a conflict is returned and they stay ADMIN, so the platform always has an admin
            """)
    void updateUserRoleReturnsConflictWhenDemotingLastActiveAdmin() {
        // Given
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = updateRole(ADMIN, "MEMBER");

        // Then
        response.expectStatus().isEqualTo(HttpStatus.CONFLICT);

        assertEquals(UserRole.ADMIN, findUser(ADMIN).getRole());
    }

    @Test
    @DisplayName("""
            Given a signed-in admin
            When they change the role of a user that does not exist, or that deleted their account
            Then a not found response is returned
            """)
    void updateUserRoleReturnsNotFoundWhenUserDoesNotExistOrIsDeleted() {
        // Given
        insertUser(1004L, UserRole.MEMBER, UserStatus.DELETED);
        actingAs(ADMIN);

        // When / Then
        updateRole(999999L, "ADMIN").expectStatus().isNotFound();
        updateRole(1004L, "ADMIN").expectStatus().isNotFound();
    }

    @Test
    @DisplayName("""
            Given a signed-in admin
            When the request has no role
            Then a bad request is returned and the role is unchanged
            """)
    void updateUserRoleReturnsBadRequestWhenRoleIsMissing() {
        // Given
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = restClient
                .put()
                .uri("/admin/users/{id}/role", AUTHOR)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{}")
                .exchange();

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("role is required");

        assertEquals(UserRole.MEMBER, findUser(AUTHOR).getRole());
    }

    @Test
    @DisplayName("""
            Given a signed-in admin
            When the request names a role that does not exist
            Then a bad request is returned
            """)
    void updateUserRoleReturnsBadRequestWhenRoleIsUnknown() {
        // Given
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = updateRole(AUTHOR, "SUPERUSER");

        // Then
        response.expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("""
            Given a signed-in member who is not an admin
            When they try to change a role, even their own
            Then a forbidden response is returned
            """)
    void updateUserRoleReturnsForbiddenWhenCallerIsNotAdmin() {
        // Given
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = updateRole(AUTHOR, "ADMIN");

        // Then
        response.expectStatus().isForbidden();

        assertEquals(UserRole.MEMBER, findUser(AUTHOR).getRole());
    }

    @Test
    @DisplayName("""
            Given a signed-in admin
            When they make a member gym staff (STAFF)
            Then the role is changed and the user is returned with HTTP 200
            """)
    void updateUserRoleReturnsOkWhenAdminMakesMemberStaff() {
        // Given
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = updateRole(AUTHOR, "STAFF");

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.role").isEqualTo("STAFF");

        assertEquals(UserRole.STAFF, findUser(AUTHOR).getRole());
    }

    @Test
    @DisplayName("""
            Given the only active admin
            When they make themselves gym staff
            Then a conflict is returned and they stay ADMIN, so the platform always has an admin
            """)
    void updateUserRoleReturnsConflictWhenLastAdminBecomesStaff() {
        // Given
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = updateRole(ADMIN, "STAFF");

        // Then
        response.expectStatus().isEqualTo(HttpStatus.CONFLICT);

        assertEquals(UserRole.ADMIN, findUser(ADMIN).getRole());
    }

    @Test
    @DisplayName("""
            Given a signed-in admin
            When they make a member gym staff
            Then the audit log records who changed whose role, and to what
            """)
    void roleChangeIsAudited(CapturedOutput output) {
        // Given
        actingAs(ADMIN);

        // When
        updateRole(AUTHOR, "STAFF").expectStatus().isOk();

        // Then
        assertTrue(output.getAll().contains("role_changed by=1003 user=1001 role=STAFF"));
    }

    @Test
    @DisplayName("""
            Given the house account, which owns the gym's content
            When the admin sends a PUT request to change its role
            Then a conflict is returned, and its role stays STAFF
            """)
    void updateUserRoleRefusesTheHouseAccount() {
        // Given
        insertHouseAccount(1005L);
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = updateRole(1005L, "MEMBER");

        // Then
        response.expectStatus().isEqualTo(409);
        assertEquals(UserRole.STAFF, findUser(1005L).getRole());
    }

    private RestTestClient.ResponseSpec updateRole(Long userId, String role) {
        return restClient
                .put()
                .uri("/admin/users/{id}/role", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "role": "%s"
                        }
                        """.formatted(role))
                .exchange();
    }
}
