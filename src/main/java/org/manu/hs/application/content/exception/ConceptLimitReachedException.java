package org.manu.hs.application.content.exception;

import org.manu.hs.application.common.exception.base.ConflictException;

/** A member owns as many concepts as they may: they retire or delete one before adding another. */
public class ConceptLimitReachedException extends ConflictException {
    public ConceptLimitReachedException(int limit) {
        super("You can have up to %d concepts: retire one to add another".formatted(limit));
    }
}
