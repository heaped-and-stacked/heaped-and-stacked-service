package org.manu.hs.api.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.manu.hs.domain.user.AuthProvider;
import org.manu.hs.domain.user.Invite;

import java.time.Instant;

/** Who may sign in while sign-in is invite-only; see Invite. */
@Getter
@AllArgsConstructor
@Builder
public class InviteResponse {

    private Long id;
    private AuthProvider provider;
    /** The username at the provider, as it was entered. */
    private String login;
    private Instant invitedAt;

    public static InviteResponse toDto(Invite invite) {
        return InviteResponse.builder()
                .id(invite.getId())
                .provider(invite.getProvider())
                .login(invite.getProviderLogin())
                .invitedAt(invite.getInvitedAt())
                .build();
    }
}
