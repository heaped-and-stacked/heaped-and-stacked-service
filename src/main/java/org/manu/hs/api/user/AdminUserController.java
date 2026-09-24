package org.manu.hs.api.user;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.manu.hs.api.user.dto.CreateInviteRequest;
import org.manu.hs.api.user.dto.InviteResponse;
import org.manu.hs.api.user.dto.OverviewResponse;
import org.manu.hs.api.user.dto.UpdateUserRoleRequest;
import org.manu.hs.api.user.dto.UpdateUserStatusRequest;
import org.manu.hs.api.user.dto.UserResponse;
import org.manu.hs.application.user.UserApplication;
import org.manu.hs.domain.user.User;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.domain.user.UserStatus;
import org.manu.hs.security.UserSessions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * User administration, and the overview at the top of the admin page. SecurityConfig lets only
 * admins reach /admin/**, and UserApplication checks the role again.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminUserController {

    private final UserApplication userApplication;
    private final UserSessions userSessions;

    /** The numbers at the top of the admin page. */
    @GetMapping("/overview")
    @ResponseStatus(HttpStatus.OK)
    public OverviewResponse getOverview() {
        return OverviewResponse.toDto(userApplication.getOverview());
    }

    /** Who may sign in while sign-in is invite-only (by username); none: anyone may. */
    @GetMapping("/invites")
    @ResponseStatus(HttpStatus.OK)
    public List<InviteResponse> getInvites() {
        return userApplication.getInvites().stream().map(InviteResponse::toDto).toList();
    }

    @PostMapping("/invites")
    @ResponseStatus(HttpStatus.CREATED)
    public InviteResponse invite(@RequestBody CreateInviteRequest request) {
        return InviteResponse.toDto(userApplication.invite(request.getLogin()));
    }

    @DeleteMapping("/invites/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInvite(@PathVariable Long id) {
        userApplication.deleteInvite(id);
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getUsers(
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) UserStatus status) {
        return userApplication.listUsers(role, status).stream().map(UserResponse::toDto).toList();
    }

    /** The user is signed out everywhere, so the new role applies from their next sign-in. */
    @PutMapping("/users/{id}/role")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateUserRole(
            @PathVariable Long id,
            @RequestBody UpdateUserRoleRequest request,
            HttpServletRequest httpRequest) {
        final User updated = userApplication.changeRole(id, request.getRole());
        userSessions.endAllSessionsOf(id, httpRequest);
        return UserResponse.toDto(updated);
    }

    /** The user is signed out everywhere, so blocking takes effect immediately. */
    @PutMapping("/users/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateUserStatus(
            @PathVariable Long id,
            @RequestBody UpdateUserStatusRequest request,
            HttpServletRequest httpRequest) {
        final User updated = userApplication.changeStatus(id, request.getStatus());
        userSessions.endAllSessionsOf(id, httpRequest);
        return UserResponse.toDto(updated);
    }

    /**
     * Deletes someone's account (soft delete, as when they delete it themselves) and signs them
     * out everywhere. eraseActivity=true also erases their sets, votes and feedback.
     */
    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean eraseActivity,
            HttpServletRequest httpRequest) {
        userApplication.deleteAccountAsAdmin(id, eraseActivity);
        userSessions.endAllSessionsOf(id, httpRequest);
    }
}
