package org.manu.hs.api.learning.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.manu.hs.domain.question.ReportReason;

/** Why the learner reports the question (required), and optionally a note of up to 2000 characters (room for a suggested fix). */
@Getter
@Setter
@NoArgsConstructor
public class ReportQuestionRequest {
    private ReportReason reason;
    private String note;
}
