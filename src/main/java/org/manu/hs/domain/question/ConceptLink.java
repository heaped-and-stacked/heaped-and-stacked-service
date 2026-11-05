package org.manu.hs.domain.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * A question's link to one of its concepts, and whether the question is accepted for it.
 */
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
public class ConceptLink {

    private Question question;
    private String conceptTag;
    private ConceptLinkStatus status;
    /** Who wrote the question: known for what waits for a decision. */
    private String authorName;

}
