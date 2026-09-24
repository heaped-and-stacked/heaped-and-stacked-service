package org.manu.hs.application.content.exception;

import org.manu.hs.application.common.exception.base.ConflictException;

public class ConceptAlreadyExistsException extends ConflictException {
    public ConceptAlreadyExistsException(String tag) {
        super("Concept with tag '%s' already exists".formatted(tag));
    }
}