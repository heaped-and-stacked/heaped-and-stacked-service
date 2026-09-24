package org.manu.hs.api.content.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.manu.hs.domain.question.QuestionStatus;

@Getter
@Setter
@NoArgsConstructor
public class UpdateQuestionStatusRequest {
    private QuestionStatus status;
}