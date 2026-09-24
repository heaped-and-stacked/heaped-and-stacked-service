package org.manu.hs.integration.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.manu.hs.api.user.dto.UserResponse;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.domain.user.UserStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * GET /admin/users/{id}
 */
class GetUserTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a signed-in admin
            When a GET request is sent for another user's id
            Then that user's profile is returned with HTTP 200
            """)
    void getUserReturnsProfileWhenCallerIsAdmin() {
        // Given
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = getUser(AUTHOR);

        // Then
        UserResponse user = response
                .expectStatus().isOk()
                .expectBody(UserResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(user);
        assertEquals(AUTHOR, user.getId());
        assertEquals(UserRole.MEMBER, user.getRole());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }

    @Test
    @DisplayName("""
            Given a signed-in admin
            When a GET request is sent for a user id that does not exist
            Then a not found response is returned
            """)
    void getUserReturnsNotFoundWhenUserDoesNotExist() {
        // Given
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = getUser(999999L);

        // Then
        response
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("User with id '999999' not found");
    }

    @Test
    @DisplayName("""
            Given a signed-in admin and a user who deleted their account
            When a GET request is sent for that user's id
            Then a not found response is returned, since deleted accounts count as gone
            """)
    void getUserReturnsNotFoundWhenUserIsDeleted() {
        // Given
        insertUser(1004L, UserRole.MEMBER, UserStatus.DELETED);
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = getUser(1004L);

        // Then
        response.expectStatus().isNotFound();
    }

    @Test
    @DisplayName("""
            Given a signed-in member who is not an admin
            When a GET request is sent for another user's id
            Then a forbidden response is returned
            """)
    void getUserReturnsForbiddenWhenCallerIsNotAdmin() {
        // Given
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = getUser(OTHER_USER);

        // Then
        response.expectStatus().isForbidden();
    }

    private RestTestClient.ResponseSpec getUser(Long userId) {
        return restClient
                .get()
                .uri("/admin/users/{id}", userId)
                .exchange();
    }
}
