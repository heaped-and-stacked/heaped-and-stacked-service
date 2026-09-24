package org.manu.hs.application.content.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.manu.hs.domain.question.QuestionType;

import java.util.List;

/**
 * Numbers are Integer on purpose: a missing value stays null, so validation can
 * report "is required" instead of seeing 0.
 */
@AllArgsConstructor
@Builder
@Getter
public class UpdateQuestionCommand {

    private String requirement;
    private String sampleSolution;
    private QuestionType type;
    private Integer difficultyScore;
    private Integer expectedDurationMinutes;
    private String source;
    private List<String> conceptTags;
}
