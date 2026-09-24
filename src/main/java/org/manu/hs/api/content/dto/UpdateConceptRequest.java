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
public class UpdateConceptRequest {
    private String name;
    private String description;

    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<String> externalReferences = new ArrayList<>();
}