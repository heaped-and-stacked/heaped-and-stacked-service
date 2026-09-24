package org.manu.hs.application.content.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class CreateConceptCommand {

    private String tag;
    private String name;
    private String description;
    private List<String> externalReferences;
}
