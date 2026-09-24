package org.manu.hs.application.content.exception;

import org.manu.hs.application.common.exception.base.ConflictException;

public class ConceptInUseException extends ConflictException {
    public ConceptInUseException(String tag) {
        super("Concept with tag '%s' is used by at least one question".formatted(tag));
    }
}
