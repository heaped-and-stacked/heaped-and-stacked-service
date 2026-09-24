package org.manu.hs.application.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.manu.hs.application.common.exception.base.ValidationException;
import org.manu.hs.application.user.command.SignInCommand;
import org.manu.hs.application.user.command.UpdateProfileCommand;
import org.manu.hs.application.user.exception.LastAdminException;
import org.manu.hs.application.user.exception.UserNotActiveException;
import org.manu.hs.domain.user.AuthProvider;
import org.manu.hs.domain.user.User;
import org.manu.hs.domain.user.UserRepository;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.domain.user.UserStatus;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit test for UserService: its validation, rules and builders, without Spring or a
 * database. The repository is a Mockito mock, only needed for the admin count.
 */
class UserServiceTest {

    private static final Instant NOW = Instant.parse("2026-09-24T10:00:00Z");

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserService userService = new UserService(userRepository, List.of("GITHUB:999", " GITHUB:123 ", ""));

    // ---------------------------------------------------------------------
    // Validation
    // ---------------------------------------------------------------------

    static Stream<Arguments> invalidDisplayNames() {
        return Stream.of(
                arguments("display name is null", null, "displayName is required"),
                arguments("display name is blank", "   ", "displayName is required"),
                arguments("display name has 201 characters", "x".repeat(201), "displayName must be at most 200 characters"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidDisplayNames")
    @DisplayName("""
            Given a profile update with an invalid display name
            When it is validated
            Then a validation error names the problem
            """)
    void validateRejectsInvalidDisplayName(String description, String displayName, String expectedMessage) {
        // Given
        UpdateProfileCommand command = UpdateProfileCommand.builder().displayName(displayName).build();

        // When
        ValidationException exception = assertThrows(ValidationException.class, () -> userService.validate(command));

        // Then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("""
            Given a profile update with a 200-character display name
            When it is validated
            Then it is accepted, as 200 is the limit
            """)
    void validateAcceptsDisplayNameAtTheLimit() {
        // Given
        UpdateProfileCommand command = UpdateProfileCommand.builder().displayName("x".repeat(200)).build();

        // When / Then
        assertDoesNotThrow(() -> userService.validate(command));
    }

    @Test
    @DisplayName("""
            Given an admin status change to DELETED
            When it is validated
            Then it is refused, as only the user can delete their own account
            """)
    void validateStatusChangeRejectsDeleted() {
        // When
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.validateStatusChange(UserStatus.DELETED));

        // Then
        assertEquals("status must be ACTIVE or BLOCKED", exception.getMessage());
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(value = UserStatus.class, names = {"ACTIVE", "BLOCKED"})
    @DisplayName("""
            Given an admin status change to ACTIVE or BLOCKED
            When it is validated
            Then it is accepted
            """)
    void validateStatusChangeAcceptsActiveAndBlocked(UserStatus status) {
        assertDoesNotThrow(() -> userService.validateStatusChange(status));
    }

    // ---------------------------------------------------------------------
    // Rules
    // ---------------------------------------------------------------------

    @ParameterizedTest(name = "{0}:{1} -> {2}")
    @CsvSource(textBlock = """
            # provider, provider id, bootstrap admin?
            GITHUB,     999,         true
            GITHUB,     123,         true
            GITHUB,     1000,        false
            """)
    @DisplayName("""
            Given the configured bootstrap admins (with surrounding spaces and an empty entry)
            When a provider account is checked
            Then only the listed accounts are bootstrap admins
            """)
    void isBootstrapAdminMatchesOnlyListedAccounts(AuthProvider provider, String providerUserId, boolean expected) {
        assertEquals(expected, userService.isBootstrapAdmin(provider, providerUserId));
    }

    @Test
    @DisplayName("""
            Given a blocked user
            When the user is required to be active
            Then they are refused
            """)
    void requireActiveRefusesBlockedUser() {
        // Given
        User blocked = aUser(UserRole.MEMBER, UserStatus.BLOCKED);

        // When / Then
        assertThrows(UserNotActiveException.class, () -> userService.requireActive(blocked));
    }

    @Test
    @DisplayName("""
            Given the only active admin
            When they would lose their active-admin standing
            Then the change is refused
            """)
    void requireNotLastActiveAdminRefusesTheOnlyActiveAdmin() {
        // Given
        when(userRepository.countActiveAdmins()).thenReturn(1);
        User admin = aUser(UserRole.ADMIN, UserStatus.ACTIVE);

        // When / Then
        assertThrows(LastAdminException.class, () -> userService.requireNotLastActiveAdmin(admin));
    }

    @Test
    @DisplayName("""
            Given an active admin while another active admin exists
            When they would lose their active-admin standing
            Then the change is allowed
            """)
    void requireNotLastActiveAdminAllowsWhenAnotherAdminRemains() {
        // Given
        when(userRepository.countActiveAdmins()).thenReturn(2);
        User admin = aUser(UserRole.ADMIN, UserStatus.ACTIVE);

        // When / Then
        assertDoesNotThrow(() -> userService.requireNotLastActiveAdmin(admin));
    }

    @Test
    @DisplayName("""
            Given a member
            When the last-admin rule is checked
            Then it passes without counting admins, since a member is never the last admin
            """)
    void requireNotLastActiveAdminIgnoresMembers() {
        // Given
        User member = aUser(UserRole.MEMBER, UserStatus.ACTIVE);

        // When
        userService.requireNotLastActiveAdmin(member);

        // Then
        verifyNoInteractions(userRepository);
    }

    // ---------------------------------------------------------------------
    // Builders
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("""
            Given a first sign-in without a name on the provider
            When the new user is built
            Then the provider username becomes the display name, and the user is ACTIVE
            """)
    void newUserFallsBackToProviderLoginForDisplayName() {
        // Given
        SignInCommand command = aSignIn(null, null);

        // When
        User user = userService.newUser(command, UserRole.MEMBER, NOW);

        // Then
        assertEquals("coder", user.getDisplayName());
        assertEquals(UserRole.MEMBER, user.getRole());
        assertEquals(UserStatus.ACTIVE, user.getStatus());
        assertEquals(NOW, user.getLastLoginAt());
    }

    @Test
    @DisplayName("""
            Given a returning user who chose their own display name
            When their sign-in is applied
            Then the display name is kept, while avatar, role and last login are updated
            """)
    void signedInUserKeepsDisplayNameAndUpdatesProviderData() {
        // Given
        User existing = aUser(UserRole.MEMBER, UserStatus.ACTIVE);
        SignInCommand command = aSignIn("Name On Provider", "https://avatars.example.com/new");

        // When
        User user = userService.signedInUser(existing, command, UserRole.ADMIN, NOW);

        // Then
        assertEquals("Chosen Name", user.getDisplayName());
        assertEquals("https://avatars.example.com/new", user.getAvatarUrl());
        assertEquals(UserRole.ADMIN, user.getRole());
        assertEquals(NOW, user.getLastLoginAt());
    }

    @Test
    @DisplayName("""
            Given a user deleting their account
            When the deleted user is built
            Then personal data is cleared and the deletion is recorded
            """)
    void deletedUserClearsPersonalData() {
        // Given
        User existing = aUser(UserRole.MEMBER, UserStatus.ACTIVE);

        // When
        User user = userService.deletedUser(existing, NOW);

        // Then
        assertNull(user.getDisplayName());
        assertNull(user.getAvatarUrl());
        assertEquals(UserStatus.DELETED, user.getStatus());
        assertEquals(NOW, user.getDeletedAt());
    }

    private static User aUser(UserRole role, UserStatus status) {
        return User.builder()
                .id(7L)
                .displayName("Chosen Name")
                .avatarUrl("https://avatars.example.com/old")
                .role(role)
                .status(status)
                .build();
    }

    private static SignInCommand aSignIn(String name, String avatarUrl) {
        return SignInCommand.builder()
                .provider(AuthProvider.GITHUB)
                .providerUserId("5001")
                .providerLogin("coder")
                .displayName(name)
                .avatarUrl(avatarUrl)
                .build();
    }
}
