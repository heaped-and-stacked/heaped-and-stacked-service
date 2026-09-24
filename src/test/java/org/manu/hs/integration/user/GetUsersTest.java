package org.manu.hs.integration.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.api.user.dto.UserResponse;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.domain.user.UserStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * GET /admin/users
 */
class GetUsersTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a signed-in admin
            When a GET request is sent to the admin users endpoint without filters
            Then all users are returned, ordered by id, with HTTP 200
            """)
    void getUsersReturnsAllUsersWhenNoFilterProvided() {
        // Given
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = getUsers("/admin/users");

        // Then
        List<UserResponse> users = response
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<UserResponse>>() {
                })
                .returnResult()
                .getResponseBody();

        assertNotNull(users);
        assertEquals(List.of(AUTHOR, OTHER_USER, ADMIN),
                users.stream().map(UserResponse::getId).toList());
    }

    @Test
    @DisplayName("""
            Given a signed-in admin
            When a GET request is sent to the admin users endpoint filtered by role
            Then only users with that role are returned
            """)
    void getUsersReturnsOnlyMatchingRoleWhenRoleFilterProvided() {
        // Given
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = getUsers("/admin/users?role=ADMIN");

        // Then
        List<UserResponse> users = response
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<UserResponse>>() {
                })
                .returnResult()
                .getResponseBody();

        assertNotNull(users);
        assertEquals(List.of(ADMIN), users.stream().map(UserResponse::getId).toList());
    }

    @Test
    @DisplayName("""
            Given a signed-in admin and a blocked member
            When a GET request is sent to the admin users endpoint filtered by status
            Then only users with that status are returned
            """)
    void getUsersReturnsOnlyMatchingStatusWhenStatusFilterProvided() {
        // Given
        insertUser(1004L, UserRole.MEMBER, UserStatus.BLOCKED);
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = getUsers("/admin/users?status=BLOCKED");

        // Then
        List<UserResponse> users = response
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<UserResponse>>() {
                })
                .returnResult()
                .getResponseBody();

        assertNotNull(users);
        assertEquals(List.of(1004L), users.stream().map(UserResponse::getId).toList());
        assertEquals(UserStatus.BLOCKED, users.getFirst().getStatus());
    }

    @Test
    @DisplayName("""
            Given a signed-in admin
            When a GET request is sent to the admin users endpoint with a role that does not exist
            Then a bad request is returned with a message
            """)
    void getUsersReturnsBadRequestWhenRoleIsUnknown() {
        // Given
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = getUsers("/admin/users?role=SUPERUSER");

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isNotEmpty();
    }

    @Test
    @DisplayName("""
            Given a signed-in member who is not an admin
            When a GET request is sent to the admin users endpoint
            Then a forbidden response is returned
            """)
    void getUsersReturnsForbiddenWhenCallerIsNotAdmin() {
        // Given
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = getUsers("/admin/users");

        // Then
        response
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Access denied");
    }

    @Test
    @DisplayName("""
            Given a visitor who is not signed in
            When a GET request is sent to the admin users endpoint
            Then an unauthorized response is returned
            """)
    void getUsersReturnsUnauthorizedWhenVisitor() {
        // Given
        actingAsVisitor();

        // When
        RestTestClient.ResponseSpec response = getUsers("/admin/users");

        // Then
        response.expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("""
            Given signed-in gym staff, who curate content but don't manage people
            When a GET request is sent to the admin users endpoint
            Then a forbidden response is returned
            """)
    void getUsersReturnsForbiddenWhenCallerIsStaff() {
        // Given
        insertUser(STAFF, UserRole.STAFF);
        actingAs(STAFF);

        // When
        RestTestClient.ResponseSpec response = getUsers("/admin/users");

        // Then
        response.expectStatus().isForbidden();
    }

    private RestTestClient.ResponseSpec getUsers(String uri) {
        return restClient
                .get()
                .uri(uri)
                .exchange();
    }
}
