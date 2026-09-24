package org.manu.hs.api.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.manu.hs.domain.user.UserRole;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserRoleRequest {
    private UserRole role;
}
