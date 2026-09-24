package org.manu.hs.api.content.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.manu.hs.domain.question.ConceptLinkStatus;

/** ACCEPTED or DECLINED: the concept owner's decision on a question for their concept. */
@Getter
@Setter
@NoArgsConstructor
public class ReviewDecisionRequest {
    private ConceptLinkStatus status;
}
