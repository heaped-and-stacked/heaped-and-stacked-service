package org.manu.hs.api.content.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.manu.hs.domain.concept.ConceptStatus;

@Getter
@Setter
@NoArgsConstructor
public class UpdateConceptStatusRequest {
    private ConceptStatus status;
}
