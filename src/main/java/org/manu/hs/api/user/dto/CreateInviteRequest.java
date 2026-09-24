package org.manu.hs.api.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A GitHub username to invite. */
@Getter
@Setter
@NoArgsConstructor
public class CreateInviteRequest {
    private String login;
}
