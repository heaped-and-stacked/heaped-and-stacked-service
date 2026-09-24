package org.manu.hs.domain.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

/**
 * Model to structure a question, be it free text, code eval, coding implementation
 */
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
public class Question {

    private Long id;
    private List<String> conceptTags;
    private String title;
    private String requirement;
    private String sampleSolution;
    private int difficultyScore;
    private QuestionStatus status;
    private String source;
    private Long createdBy;
    private Long updatedBy;
    private Instant createdAt;
    private Instant updatedAt;

}