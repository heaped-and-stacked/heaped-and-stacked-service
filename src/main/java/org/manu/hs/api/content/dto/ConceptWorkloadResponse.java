package org.manu.hs.api.content.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.manu.hs.domain.concept.ConceptWorkload;

/** One concept's workload, for whoever curates it. */
@AllArgsConstructor
@Builder
@Getter
public class ConceptWorkloadResponse {

    private String tag;
    private int questions;
    private int toVerify;
    private int waiting;

    public static ConceptWorkloadResponse toDto(ConceptWorkload workload) {
        return ConceptWorkloadResponse.builder()
                .tag(workload.getTag())
                .questions(workload.getQuestions())
                .toVerify(workload.getToVerify())
                .waiting(workload.getWaiting())
                .build();
    }
}
