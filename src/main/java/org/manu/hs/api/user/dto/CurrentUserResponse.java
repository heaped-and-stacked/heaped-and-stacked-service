package org.manu.hs.api.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.manu.hs.domain.user.User;
import org.manu.hs.domain.user.UserRole;
import org.manu.hs.domain.user.UserStatus;

import java.time.Instant;

/**
 * The signed-in user's own profile (GET /users/me): what UserResponse holds, plus what the web
 * app needs to know about this user's rights.
 */
@Getter
@AllArgsConstructor
@Builder
public class CurrentUserResponse {

    private Long id;
    private String displayName;
    private String avatarUrl;
    private UserRole role;
    private UserStatus status;
    private Instant createdAt;
    private Instant lastLoginAt;
    /** Whether they may add questions: staff always, members when it's open to them. */
    private boolean canAddQuestions;
    /** Whether they may add concepts: staff always, members when it's open to them. */
    private boolean canAddConcepts;

    public static CurrentUserResponse toDto(User user, boolean canAddQuestions, boolean canAddConcepts) {
        return CurrentUserResponse.builder()
                .id(user.getId())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .canAddQuestions(canAddQuestions)
                .canAddConcepts(canAddConcepts)
                .build();
    }
}
