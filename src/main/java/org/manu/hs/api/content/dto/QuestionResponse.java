package org.manu.hs.api.content.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.manu.hs.domain.question.Question;
import org.manu.hs.domain.question.QuestionStatus;

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
    private Long createdBy;
    private Long updatedBy;
    private Instant createdAt;
    private Instant updatedAt;

    public static QuestionResponse toDto(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .title(question.getTitle())
                .requirement(question.getRequirement())
                .sampleSolution(question.getSampleSolution())
                .difficultyScore(question.getDifficultyScore())
                .status(question.getStatus())
                .source(question.getSource())
                .conceptTags(List.copyOf(question.getConceptTags()))
                .createdBy(question.getCreatedBy())
                .updatedBy(question.getUpdatedBy())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .build();
    }
}