package org.manu.hs.api.learning.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubmitAnswerRequest {
    private Long questionId;
    private String answer;
}
