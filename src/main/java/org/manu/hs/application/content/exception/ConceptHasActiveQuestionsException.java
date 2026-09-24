package org.manu.hs.application.content.exception;

import org.manu.hs.application.common.exception.base.ConflictException;

/**
 * A concept used by an ACTIVE question may not go back to DRAFT: that question would
 * then link a topic that isn't approved. A 409, because the request is valid; it's the
 * questions using the concept that block it.
 */
public class ConceptHasActiveQuestionsException extends ConflictException {
    public ConceptHasActiveQuestionsException(String tag) {
        super("Concept '%s' can't go back to draft while active questions use it".formatted(tag));
    }
}
