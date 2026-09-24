package org.manu.hs.api.user;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.manu.hs.api.user.dto.UpdateUserRoleRequest;
import org.manu.hs.api.user.dto.UpdateUserStatusRequest;
import org.manu.hs.api.user.dto.UserResponse;
import org.manu.hs.application.user.UserApplication;
import org.manu.hs.domain.user.User;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.domain.user.UserStatus;
import org.manu.hs.security.UserSessions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * User administration. SecurityConfig lets only admins reach /admin/**, and
 * UserApplication checks the role again.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserApplication userApplication;
    private final UserSessions userSessions;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getUsers(
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) UserStatus status) {
        return userApplication.listUsers(role, status).stream().map(UserResponse::toDto).toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUser(@PathVariable Long id) {
        return UserResponse.toDto(userApplication.getUserAsAdmin(id));
    }

    /** The user is signed out everywhere, so the new role applies from their next sign-in. */
    @PutMapping("/{id}/role")
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
    @PutMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateUserStatus(
            @PathVariable Long id,
            @RequestBody UpdateUserStatusRequest request,
            HttpServletRequest httpRequest) {
        final User updated = userApplication.changeStatus(id, request.getStatus());
        userSessions.endAllSessionsOf(id, httpRequest);
        return UserResponse.toDto(updated);
    }
}
