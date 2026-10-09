package org.manu.hs.api.learning.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.manu.hs.domain.question.Vote;

/** The learner's vote on a question: UP or DOWN. */
@Getter
@Setter
@NoArgsConstructor
public class VoteRequest {
    private Vote vote;
}
