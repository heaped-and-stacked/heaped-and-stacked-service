package org.manu.hs.application.learning.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** The learner's optional self-assessment of an answer; both values are 1 to 10, or left out. */
@AllArgsConstructor
@Builder
@Getter
public class AssessAnswerCommand {

    private Integer selfGrade;
    private Integer perceivedDifficulty;

}
