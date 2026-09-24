package org.manu.hs.api.content.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.manu.hs.domain.concept.Concept;
import org.manu.hs.domain.concept.ConceptStatus;
import org.manu.hs.domain.concept.ContributionPolicy;

import java.time.Instant;
import java.util.List;


@AllArgsConstructor
@Builder
@Getter
public class ConceptResponse {

    private String tag;
    private String name;
    private String description;
    private List<String> externalReferences;
    private ConceptStatus status;
    private ContributionPolicy contributionPolicy;
    /** In the gym's pool ("all muscles"); otherwise a member's own, in the community list. */
    private boolean featured;
    /** The gym's own (the house account's): it always stays in the gym's pool. */
    private boolean houseOwned;
    private Long createdBy;
    private Long updatedBy;
    private Instant createdAt;
    private Instant updatedAt;

    public static ConceptResponse toDto(Concept concept) {
        return ConceptResponse.builder()
                .tag(concept.getTag())
                .name(concept.getName())
                .description(concept.getDescription())
                .externalReferences(List.copyOf(concept.getExternalReferences()))
                .status(concept.getStatus())
                .contributionPolicy(concept.getContributionPolicy())
                .featured(concept.isFeatured())
                .houseOwned(concept.isHouseOwned())
                .createdBy(concept.getCreatedBy())
                .updatedBy(concept.getUpdatedBy())
                .createdAt(concept.getCreatedAt())
                .updatedAt(concept.getUpdatedAt())
                .build();
    }
}
