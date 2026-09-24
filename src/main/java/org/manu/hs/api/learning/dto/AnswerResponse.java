package org.manu.hs.api.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.manu.hs.api.content.dto.QuestionResponse;
import org.manu.hs.domain.answer.Answer;

import java.time.Instant;

@Getter
@AllArgsConstructor
@Builder
public class AnswerResponse {

    private Long id;
    private Long questionId;
    private String answer;
    /** The question as it was when answered, including its sample solution. */
    private QuestionResponse question;
    private Integer selfGrade;
    private Integer perceivedDifficulty;
    private Instant answeredAt;

    public static AnswerResponse toDto(Answer answer) {
        return AnswerResponse.builder()
                .id(answer.getId())
                .questionId(answer.getQuestionId())
                .answer(answer.getAnswer())
                .question(QuestionResponse.toDto(answer.getQuestion()))
                .selfGrade(answer.getSelfGrade())
                .perceivedDifficulty(answer.getPerceivedDifficulty())
                .answeredAt(answer.getAnsweredAt())
                .build();
    }
}
