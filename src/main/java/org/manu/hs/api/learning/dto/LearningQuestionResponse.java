package org.manu.hs.api.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.manu.hs.domain.question.Question;

import java.util.List;

/**
 * A question to answer. Deliberately without the sample solution: learners get it with the
 * response to their answer.
 */
@Getter
@AllArgsConstructor
@Builder
public class LearningQuestionResponse {

    private Long id;
    private String title;
    private String requirement;
    private int difficultyScore;
    private String source;
    /** Only the concepts the question is accepted for. */
    private List<String> conceptTags;
    /**
     * How many questions for the requested concepts this learner has never answered, this one
     * included if it's new to them. 0: they've all been answered at least once.
     */
    private int unansweredCount;

    public static LearningQuestionResponse toDto(Question question, int unansweredCount) {
        return LearningQuestionResponse.builder()
                .id(question.getId())
                .title(question.getTitle())
                .requirement(question.getRequirement())
                .difficultyScore(question.getDifficultyScore())
                .source(question.getSource())
                .conceptTags(List.copyOf(question.getConceptTags()))
                .unansweredCount(unansweredCount)
                .build();
    }
}
