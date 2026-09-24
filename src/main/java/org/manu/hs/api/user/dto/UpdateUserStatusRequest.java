package org.manu.hs.api.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.manu.hs.domain.user.UserStatus;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserStatusRequest {
    private UserStatus status;
}
