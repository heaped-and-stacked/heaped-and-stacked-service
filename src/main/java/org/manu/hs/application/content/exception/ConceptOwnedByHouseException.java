package org.manu.hs.application.content.exception;

import org.manu.hs.application.common.exception.base.ConflictException;

/**
 * House-owned concepts stay featured: nobody can sign in as the house account that owns
 * them, so out of the pool nobody would curate them.
 */
public class ConceptOwnedByHouseException extends ConflictException {
    public ConceptOwnedByHouseException(String tag) {
        super("Concept '%s' is owned by the house account: it stays featured".formatted(tag));
    }
}
