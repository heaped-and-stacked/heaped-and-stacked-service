package org.manu.hs.api.content.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.manu.hs.domain.question.Question;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.question.VoteTotals;

import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class QuestionResponse {

    private Long id;
    private String title;
    private String requirement;
    private String sampleSolution;
    private int difficultyScore;
    private QuestionStatus status;
    private String source;
    private List<String> conceptTags;
    /** Verified: checked by whoever curates it (a clear question, a correct answer). */
    private boolean verified;
    private Long createdBy;
    private Long updatedBy;
    private Instant createdAt;
    private Instant updatedAt;
    /** How many learners voted it up and down; counted for curators (the list and one question), 0 elsewhere. */
    private int upVotes;
    private int downVotes;

    public static QuestionResponse toDto(Question question) {
        return toDto(question, VoteTotals.NONE);
    }

    public static QuestionResponse toDto(Question question, VoteTotals votes) {
        return QuestionResponse.builder()
                .id(question.getId())
                .title(question.getTitle())
                .requirement(question.getRequirement())
                .sampleSolution(question.getSampleSolution())
                .difficultyScore(question.getDifficultyScore())
                .status(question.getStatus())
                .source(question.getSource())
                .conceptTags(List.copyOf(question.getConceptTags()))
                .verified(question.isVerified())
                .createdBy(question.getCreatedBy())
                .updatedBy(question.getUpdatedBy())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .upVotes(votes.getUp())
                .downVotes(votes.getDown())
                .build();
    }
}