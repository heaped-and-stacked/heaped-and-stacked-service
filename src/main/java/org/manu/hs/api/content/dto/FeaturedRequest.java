package org.manu.hs.api.content.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** true: the concept becomes featured; false: back to its owner's own. */
@Getter
@Setter
@NoArgsConstructor
public class FeaturedRequest {
    private Boolean featured;
}
