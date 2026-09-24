package org.manu.hs.domain.concept;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
public class Concept {

    private String tag;
    private String name;
    private String description;
    private List<String> externalReferences;
    private ConceptStatus status;
    private ContributionPolicy contributionPolicy;
    /** Featured: trained when no concept is picked, curated by staff; otherwise its owner's own (custom). */
    private boolean featured;
    /** House-owned: owned by the house account. Always featured, curated by staff. */
    private boolean houseOwned;
    /** The author, who is also the concept's owner. */
    private Long createdBy;
    private Long updatedBy;
    private Instant createdAt;
    private Instant updatedAt;

    /**
     * Who curates this concept: edits it, decides which questions it accepts, and verifies them.
     * Its owner; and staff (admins included) while it's featured. A member's
     * own concept is theirs alone. The one place this rule lives: co-owners, if they ever come,
     * are added here.
     */
    public boolean isCuratedBy(Long userId, boolean staff) {
        return createdBy.equals(userId) || (staff && featured);
    }

}
