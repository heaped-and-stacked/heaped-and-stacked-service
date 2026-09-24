package org.manu.hs.api.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.manu.hs.domain.user.User;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.domain.user.UserStatus;

import java.time.Instant;

@Getter
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String displayName;
    private String avatarUrl;
    private UserRole role;
    private UserStatus status;
    private Instant createdAt;
    private Instant lastLoginAt;

    public static UserResponse toDto(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}
