package org.manu.hs.application.user.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class UpdateProfileCommand {

    private String displayName;
}
