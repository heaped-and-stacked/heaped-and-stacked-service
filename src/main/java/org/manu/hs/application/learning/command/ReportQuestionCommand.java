package org.manu.hs.application.learning.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.manu.hs.domain.question.ReportReason;

/** A learner's report on a question: why it's wrong or unsuitable, and optionally a note. */
@AllArgsConstructor
@Builder
@Getter
public class ReportQuestionCommand {

    private ReportReason reason;
    private String note;

}
