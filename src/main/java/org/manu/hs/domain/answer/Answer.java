package org.manu.hs.domain.answer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.manu.hs.domain.question.Question;

import java.time.Instant;

/**
 * A learner's answer to a question, with their optional self-assessment.
 */
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
public class Answer {

    private Long id;
    private Long userId;
    private Long questionId;
    /** The question exactly as it was when answered; later edits to the question don't change it. */
    private Question question;
    private String answer;
    /** How well the learner thinks they did, 1 to 10. Optional. */
    private Integer selfGrade;
    /** How hard the question felt to the learner, 1 to 10. Optional. */
    private Integer perceivedDifficulty;
    private Instant answeredAt;

}
