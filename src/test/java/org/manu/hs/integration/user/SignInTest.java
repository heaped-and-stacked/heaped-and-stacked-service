package org.manu.hs.integration.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.manu.hs.application.user.UserApplication;
import org.manu.hs.application.user.command.SignInCommand;
import org.manu.hs.application.user.exception.SignInNotInvitedException;
import org.manu.hs.application.user.exception.UserNotActiveException;
import org.manu.hs.domain.user.AuthProvider;
import org.manu.hs.domain.user.User;
import org.manu.hs.domain.user.UserIdentity;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.domain.user.UserStatus;
import org.manu.hs.integration.AbstractIntegrationTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Sign-in: what happens when GitHub confirms who signed in.
 * <p>
 * GitHub can't take part in tests, so this calls UserApplication.signIn directly: the
 * exact call the login flow makes after GitHub's callback (OAuth2SignInService).
 * The test profile makes GitHub account 999 a bootstrap admin.
 */
@ExtendWith(OutputCaptureExtension.class)
class SignInTest extends AbstractIntegrationTest {

    private static final String BOOTSTRAP_ADMIN_GITHUB_ID = "999";

    @Autowired
    private UserApplication userApplication;

    @Test
    @DisplayName("""
            Given a GitHub account that has never signed in
            When it signs in
            Then a new ACTIVE member is registered, with the GitHub account linked to it
            """)
    void signInRegistersNewMemberOnFirstSignIn() {
        // Given
        SignInCommand command = gitHubSignIn("5001", "new-coder", "New Coder", "https://avatars.example.com/new");

        // When
        User user = userApplication.signIn(command);

        // Then
        assertNotNull(user.getId());
        assertEquals(UserRole.MEMBER, user.getRole());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
        assertEquals("New Coder", user.getDisplayName());
        assertEquals("https://avatars.example.com/new", user.getAvatarUrl());
        assertNotNull(user.getLastLoginAt());

        // Verify database entry: the GitHub account is linked to the new user
        UserIdentity identity = findIdentity(AuthProvider.GITHUB, "5001");
        assertEquals(user.getId(), identity.getUserId());
        assertEquals("new-coder", identity.getProviderLogin());
    }

    @Test
    @DisplayName("""
            Given a GitHub account without a public name
            When it signs in for the first time
            Then the GitHub username is used as the display name
            """)
    void signInUsesUsernameAsDisplayNameWhenNameIsMissing() {
        // Given
        SignInCommand command = gitHubSignIn("5001", "new-coder", null, null);

        // When
        User user = userApplication.signIn(command);

        // Then
        assertEquals("new-coder", user.getDisplayName());
    }

    @Test
    @DisplayName("""
            Given the GitHub account configured as a bootstrap admin
            When it signs in for the first time
            Then the new user is an ADMIN
            """)
    void signInRegistersAdminWhenAccountIsBootstrapAdmin() {
        // Given
        SignInCommand command = gitHubSignIn(BOOTSTRAP_ADMIN_GITHUB_ID, "the-owner", "The Owner", null);

        // When
        User user = userApplication.signIn(command);

        // Then
        assertEquals(UserRole.ADMIN, user.getRole());
    }

    @Test
    @DisplayName("""
            Given a member who has signed in before and changed their display name here
            When they sign in again after renaming themselves and changing their avatar on GitHub
            Then it is the same user; the role and display name are kept, the avatar and GitHub username are refreshed
            """)
    void signInRefreshesProviderDataButKeepsUserChoicesForReturningUser() {
        // Given
        insertIdentity(AUTHOR, AuthProvider.GITHUB, "5001", "old-login");
        SignInCommand command = gitHubSignIn("5001", "new-login", "Name On GitHub", "https://avatars.example.com/changed");

        // When
        User user = userApplication.signIn(command);

        // Then
        assertEquals(AUTHOR, user.getId());
        assertEquals(UserRole.MEMBER, user.getRole());
        assertEquals("User " + AUTHOR, user.getDisplayName());
        assertEquals("https://avatars.example.com/changed", user.getAvatarUrl());
        assertNotNull(user.getLastLoginAt());

        // Verify database entry: the GitHub username follows the rename
        UserIdentity identity = findIdentity(AuthProvider.GITHUB, "5001");
        assertEquals("new-login", identity.getProviderLogin());
        assertNotNull(identity.getLastLoginAt());
    }

    @Test
    @DisplayName("""
            Given a bootstrap admin who was demoted to member
            When they sign in again
            Then they are ADMIN again, since the bootstrap list is applied on every sign-in
            """)
    void signInRestoresAdminRoleForBootstrapAdmin() {
        // Given
        insertUser(1004L, UserRole.MEMBER);
        insertIdentity(1004L, AuthProvider.GITHUB, BOOTSTRAP_ADMIN_GITHUB_ID, "the-owner");

        // When
        User user = userApplication.signIn(gitHubSignIn(BOOTSTRAP_ADMIN_GITHUB_ID, "the-owner", "The Owner", null));

        // Then
        assertEquals(UserRole.ADMIN, user.getRole());
        assertEquals(UserRole.ADMIN, findUser(1004L).getRole());
    }

    @Test
    @DisplayName("""
            Given a blocked member
            When they sign in with GitHub
            Then the sign-in is refused
            """)
    void signInRefusesBlockedUser() {
        // Given
        insertUser(1004L, UserRole.MEMBER, UserStatus.BLOCKED);
        insertIdentity(1004L, AuthProvider.GITHUB, "5004", "blocked-coder");

        // When / Then
        UserNotActiveException exception = assertThrows(
                UserNotActiveException.class,
                () -> userApplication.signIn(gitHubSignIn("5004", "blocked-coder", null, null)));

        assertEquals("User '1004' can't sign in: the account is blocked", exception.getMessage());
    }

    @Test
    @DisplayName("""
            Given a member who deleted their account (their GitHub link was removed)
            When the same GitHub account signs in again
            Then a new, separate member is registered and the deleted account stays deleted
            """)
    void signInRegistersNewUserAfterAccountDeletion() {
        // Given
        insertUser(1004L, UserRole.MEMBER, UserStatus.DELETED);

        // When
        User user = userApplication.signIn(gitHubSignIn("5004", "returning-coder", "Returning Coder", null));

        // Then
        assertNotEquals(1004L, user.getId());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
        assertEquals(UserStatus.DELETED, findUser(1004L).getStatus());
    }

    @Test
    @DisplayName("""
            Given a new GitHub account, and a blocked member
            When both sign in
            Then the audit log records the new member's sign-in and the blocked member's refusal,
            by id only
            """)
    void signInIsAudited(CapturedOutput output) {
        // Given
        insertUser(1004L, UserRole.MEMBER, UserStatus.BLOCKED);
        insertIdentity(1004L, AuthProvider.GITHUB, "5004", "blocked-coder");

        // When
        User user = userApplication.signIn(gitHubSignIn("5001", "new-coder", "New Coder", null));
        assertThrows(UserNotActiveException.class,
                () -> userApplication.signIn(gitHubSignIn("5004", "blocked-coder", null, null)));

        // Then
        assertTrue(output.getAll().contains("sign_in user=%d role=MEMBER".formatted(user.getId())));
        assertTrue(output.getAll().contains("sign_in_refused user=1004 status=BLOCKED"));
        assertFalse(output.getAll().contains("new-coder"), "no GitHub login in the audit log");
    }

    @Test
    @DisplayName("""
            Given sign-in is invite-only (one invite exists), and a GitHub account that isn't invited
            When it signs in
            Then the sign-in is refused, and no account is registered for it
            """)
    void signInRefusesAnAccountThatIsNotInvited() {
        // Given
        insertInvite("friend");
        int usersBefore = countUsers();

        // When / Then
        SignInNotInvitedException exception = assertThrows(
                SignInNotInvitedException.class,
                () -> userApplication.signIn(gitHubSignIn("5001", "stranger", "A Stranger", null)));

        assertEquals("GitHub account 'stranger' isn't invited: sign-in is invite-only", exception.getMessage());
        assertEquals(usersBefore, countUsers());
    }

    @Test
    @DisplayName("""
            Given sign-in is invite-only, and an invite for a username written in another case
            When that GitHub account signs in
            Then it's registered as a member: usernames match in any case
            """)
    void signInRegistersAnInvitedAccountWhateverTheCase() {
        // Given
        insertInvite("New-Coder");

        // When
        User user = userApplication.signIn(gitHubSignIn("5001", "new-coder", "New Coder", null));

        // Then
        assertEquals(UserRole.MEMBER, user.getRole());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }

    @Test
    @DisplayName("""
            Given sign-in is invite-only, and a member who signed in before but isn't invited
            When they sign in again
            Then the sign-in is refused: the invite list decides for everyone
            """)
    void signInRefusesAnExistingMemberWhoIsNotInvited() {
        // Given
        insertUser(1004L, UserRole.MEMBER, UserStatus.ACTIVE);
        insertIdentity(1004L, AuthProvider.GITHUB, "5004", "old-friend");
        insertInvite("friend");

        // When / Then
        assertThrows(SignInNotInvitedException.class,
                () -> userApplication.signIn(gitHubSignIn("5004", "old-friend", null, null)));
    }

    @Test
    @DisplayName("""
            Given sign-in is invite-only, and the bootstrap admin isn't on the list
            When the bootstrap admin signs in
            Then they're signed in as admin: whoever runs the environment can't be locked out
            """)
    void signInAlwaysLetsTheBootstrapAdminIn() {
        // Given
        insertInvite("friend");

        // When
        User user = userApplication.signIn(gitHubSignIn(BOOTSTRAP_ADMIN_GITHUB_ID, "owner", "Owner", null));

        // Then
        assertEquals(UserRole.ADMIN, user.getRole());
    }

    private static SignInCommand gitHubSignIn(String gitHubId, String login, String name, String avatarUrl) {
        return SignInCommand.builder()
                .provider(AuthProvider.GITHUB)
                .providerUserId(gitHubId)
                .providerLogin(login)
                .displayName(name)
                .avatarUrl(avatarUrl)
                .build();
    }
}
