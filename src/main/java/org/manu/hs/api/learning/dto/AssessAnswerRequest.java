package org.manu.hs.api.learning.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Both optional, both 1 to 10: how well the learner did, and how hard it felt. */
@Getter
@Setter
@NoArgsConstructor
public class AssessAnswerRequest {
    private Integer selfGrade;
    private Integer perceivedDifficulty;
}
