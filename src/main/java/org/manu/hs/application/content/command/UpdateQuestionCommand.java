package org.manu.hs.application.content.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Numbers are Integer on purpose: a missing value stays null, so validation can
 * report "is required" instead of seeing 0.
 */
@AllArgsConstructor
@Builder
@Getter
public class UpdateQuestionCommand {

    private String title;
    private String requirement;
    private String sampleSolution;
    private Integer difficultyScore;
    private String source;
    private List<String> conceptTags;
}
