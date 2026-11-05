package org.manu.hs.integration.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.question.ConceptLinkStatus;
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
 * PUT /admin/users/{id}/status
 */
@ExtendWith(OutputCaptureExtension.class)
class UpdateUserStatusTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("""
            Given a signed-in admin
            When they block a member
            Then the member is BLOCKED and returned with HTTP 200
            """)
    void updateUserStatusReturnsOkWhenAdminBlocksMember() {
        // Given
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = updateStatus(AUTHOR, "BLOCKED");

        // Then
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("BLOCKED");

        assertEquals(UserStatus.BLOCKED, findUser(AUTHOR).getStatus());
    }

    @Test
    @DisplayName("""
            Given a member who is signed in elsewhere
            When an admin blocks them
            Then their very next request is refused, as blocking signs them out everywhere
            """)
    void updateUserStatusSignsOutBlockedUserImmediately() {
        // Given
        RestTestClient membersBrowser = clientSignedInAs(AUTHOR);
        actingAs(ADMIN);

        // When
        updateStatus(AUTHOR, "BLOCKED").expectStatus().isOk();

        // Then
        membersBrowser
                .get()
                .uri("/users/me")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("""
            Given a blocked member
            When an admin unblocks them
            Then the member is ACTIVE again with HTTP 200
            """)
    void updateUserStatusReturnsOkWhenAdminUnblocksMember() {
        // Given
        insertUser(1004L, UserRole.MEMBER, UserStatus.BLOCKED);
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = updateStatus(1004L, "ACTIVE");

        // Then
        response.expectStatus().isOk();

        assertEquals(UserStatus.ACTIVE, findUser(1004L).getStatus());
    }

    @Test
    @DisplayName("""
            Given a signed-in admin
            When they try to set a user's status to DELETED
            Then a bad request is returned, as only users can delete their own account
            """)
    void updateUserStatusReturnsBadRequestWhenStatusIsDeleted() {
        // Given
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = updateStatus(AUTHOR, "DELETED");

        // Then
        response
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("status must be ACTIVE or BLOCKED");

        assertEquals(UserStatus.ACTIVE, findUser(AUTHOR).getStatus());
    }

    @Test
    @DisplayName("""
            Given the signed-in admin is the last active admin
            When they block themselves
            Then a conflict is returned and they stay ACTIVE
            """)
    void updateUserStatusReturnsConflictWhenBlockingLastActiveAdmin() {
        // Given
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = updateStatus(ADMIN, "BLOCKED");

        // Then
        response.expectStatus().isEqualTo(HttpStatus.CONFLICT);

        assertEquals(UserStatus.ACTIVE, findUser(ADMIN).getStatus());
    }

    @Test
    @DisplayName("""
            Given a signed-in admin
            When they change the status of a user that does not exist
            Then a not found response is returned
            """)
    void updateUserStatusReturnsNotFoundWhenUserDoesNotExist() {
        // Given
        actingAs(ADMIN);

        // When / Then
        updateStatus(999999L, "BLOCKED").expectStatus().isNotFound();
    }

    @Test
    @DisplayName("""
            Given a signed-in member who is not an admin
            When they try to block another member
            Then a forbidden response is returned
            """)
    void updateUserStatusReturnsForbiddenWhenCallerIsNotAdmin() {
        // Given
        actingAs(AUTHOR);

        // When
        RestTestClient.ResponseSpec response = updateStatus(OTHER_USER, "BLOCKED");

        // Then
        response.expectStatus().isForbidden();

        assertEquals(UserStatus.ACTIVE, findUser(OTHER_USER).getStatus());
    }

    @Test
    @DisplayName("""
            Given a signed-in admin
            When they block a member, then unblock them
            Then the audit log records both, with who did it
            """)
    void statusChangeIsAudited(CapturedOutput output) {
        // Given
        actingAs(ADMIN);

        // When
        updateStatus(AUTHOR, "BLOCKED").expectStatus().isOk();
        updateStatus(AUTHOR, "ACTIVE").expectStatus().isOk();

        // Then
        assertTrue(output.getAll().contains("status_changed by=1003 user=1001 status=BLOCKED"));
        assertTrue(output.getAll().contains("status_changed by=1003 user=1001 status=ACTIVE"));
    }

    @Test
    @DisplayName("""
            Given the house account, which owns the gym's content
            When the admin sends a PUT request to block it
            Then a conflict is returned, and it stays active
            """)
    void updateUserStatusRefusesTheHouseAccount() {
        // Given
        insertHouseAccount(1005L);
        actingAs(ADMIN);

        // When
        RestTestClient.ResponseSpec response = updateStatus(1005L, "BLOCKED");

        // Then
        response.expectStatus().isEqualTo(409);
        assertEquals(UserStatus.ACTIVE, findUser(1005L).getStatus());
    }

    @Test
    @DisplayName("""
            Given a member with an exercise waiting for approval, and a concept of their own
            When an admin blocks them
            Then what they had waiting is declined, and their concept passes to the house account
            """)
    void blockingAMemberDeclinesWhatTheyHaveWaitingAndHandsOverTheirConcepts() {
        // Given
        Long house = insertHouseAccount();
        insertConcept("java-core");
        insertCustomConcept("my-katas", ConceptStatus.ACTIVE, OTHER_USER);
        Long waiting = insertQuestionAwaitingApproval(OTHER_USER, "java-core");
        actingAs(ADMIN);

        // When
        updateStatus(OTHER_USER, "BLOCKED").expectStatus().isOk();

        // Then
        assertEquals(ConceptLinkStatus.DECLINED, findConceptLinkStatus(waiting, "java-core"));
        assertEquals(ADMIN, findConceptLinkDecidedBy(waiting, "java-core"));
        assertEquals(house, findConceptByTag("my-katas").getCreatedBy());
    }

    private RestTestClient.ResponseSpec updateStatus(Long userId, String status) {
        return restClient
                .put()
                .uri("/admin/users/{id}/status", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "status": "%s"
                        }
                        """.formatted(status))
                .exchange();
    }
}
