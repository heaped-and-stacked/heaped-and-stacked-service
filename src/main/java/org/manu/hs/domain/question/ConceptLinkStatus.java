package org.manu.hs.domain.question;

/**
 * Whether a question is accepted for one of its concepts. Decided per concept: a question can
 * be accepted for one concept and still wait, or be declined, for another.
 * PENDING: waiting for the concept owner's approval. ACCEPTED: shown for the concept.
 * DECLINED: turned down for the concept by its owner or staff (admins included).
 */
public enum ConceptLinkStatus {
    PENDING,
    ACCEPTED,
    DECLINED
}
