package org.manu.hs.api.content.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UpdateQuestionRequest {
    private String title;
    private String requirement;
    private String sampleSolution;
    private Integer difficultyScore;
    private String source;

    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<String> conceptTags = new ArrayList<>();
}
