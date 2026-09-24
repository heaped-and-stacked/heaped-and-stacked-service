package org.manu.hs.domain.concept;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** What waits on one concept for whoever curates it. */
@AllArgsConstructor
@Builder
@Getter
public class ConceptWorkload {

    private String tag;
    /** Live questions accepted for the concept. */
    private int questions;
    /** Of those, the ones not yet verified for it. */
    private int toVerify;
    /** Submitted questions waiting for the concept owner's approval. */
    private int waiting;
}
