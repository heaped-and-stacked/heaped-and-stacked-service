package org.manu.hs.application.content.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.manu.hs.domain.concept.ContributionPolicy;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class UpdateConceptCommand {

    private String name;
    private String description;
    private List<String> externalReferences;
    /** Optional: when left out, the concept keeps its current policy. */
    private ContributionPolicy contributionPolicy;
}
