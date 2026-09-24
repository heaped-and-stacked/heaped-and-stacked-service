package org.manu.hs.api.content.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.manu.hs.domain.question.QuestionType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateQuestionRequest {
    private String requirement;
    private String sampleSolution;
    private QuestionType type;
    private Integer difficultyScore;
    private Integer expectedDurationMinutes;
    private String source;

    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<String> conceptTags = new ArrayList<>();
}
