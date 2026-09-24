package org.manu.hs.api.user;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.manu.hs.api.user.dto.UpdateProfileRequest;
import org.manu.hs.api.user.dto.UserResponse;
import org.manu.hs.application.user.UserApplication;
import org.manu.hs.application.user.command.UpdateProfileCommand;
import org.manu.hs.domain.user.User;
import org.manu.hs.security.SignedInUser;
import org.manu.hs.security.UserSessions;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * The signed-in user's own account. Every endpoint here requires signing in
 * (SecurityConfig); the user comes from the session, never from the request.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/me")
public class UserController {

    private final UserApplication userApplication;
    private final UserSessions userSessions;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getCurrentUser(@AuthenticationPrincipal SignedInUser signedInUser) {
        return UserResponse.toDto(userApplication.getUser(signedInUser.getUserId()));
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateCurrentUser(
            @AuthenticationPrincipal SignedInUser signedInUser,
            @RequestBody UpdateProfileRequest request) {
        final User updated = userApplication.updateProfile(signedInUser.getUserId(), toCommand(request));
        return UserResponse.toDto(updated);
    }

    /** Soft deletes the account, then signs the user out on every device. */
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrentUser(
            @AuthenticationPrincipal SignedInUser signedInUser,
            HttpServletRequest request) {
        userApplication.deleteAccount(signedInUser.getUserId());
        userSessions.endAllSessionsOf(signedInUser.getUserId(), request);
    }

    // ---------------------------------------------------------------------
    // Mapping: request DTOs to application commands
    // ---------------------------------------------------------------------

    private UpdateProfileCommand toCommand(UpdateProfileRequest request) {
        return UpdateProfileCommand.builder()
                .displayName(request.getDisplayName())
                .build();
    }
}
