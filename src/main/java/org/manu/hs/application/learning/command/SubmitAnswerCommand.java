package org.manu.hs.application.learning.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class SubmitAnswerCommand {

    private Long questionId;
    private String answer;

}
