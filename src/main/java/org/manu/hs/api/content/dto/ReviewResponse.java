package org.manu.hs.api.content.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.manu.hs.domain.question.ConceptLink;

/**
 * A question waiting for approval on one concept. A question waiting on several concepts
 * appears once per concept.
 */
@Getter
@AllArgsConstructor
@Builder
public class ReviewResponse {

    private String conceptTag;
    private QuestionResponse question;

    public static ReviewResponse toDto(ConceptLink link) {
        return ReviewResponse.builder()
                .conceptTag(link.getConceptTag())
                .question(QuestionResponse.toDto(link.getQuestion()))
                .build();
    }
}
