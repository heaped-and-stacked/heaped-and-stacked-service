package org.manu.hs.api.content.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** true: the question is verified (checked for quality); false: taken back. */
@Getter
@Setter
@NoArgsConstructor
public class VerificationRequest {
    private Boolean verified;
}
