package org.manu.hs.api.content.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.manu.hs.domain.question.Question;
import org.manu.hs.domain.question.QuestionStatus;
import org.manu.hs.domain.question.QuestionType;

import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class QuestionResponse {

    private Long id;
    private String requirement;
    private String sampleSolution;
    private QuestionType type;
    private int difficultyScore;
    private int expectedDurationMinutes;
    private QuestionStatus status;
    private String source;
    private List<String> conceptTags;
    private String createdBy;
    private String updatedBy;
    private Instant createdAt;
    private Instant updatedAt;

    public static QuestionResponse toDto(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .requirement(question.getRequirement())
                .sampleSolution(question.getSampleSolution())
                .type(question.getType())
                .difficultyScore(question.getDifficultyScore())
                .expectedDurationMinutes(question.getExpectedDurationMinutes())
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