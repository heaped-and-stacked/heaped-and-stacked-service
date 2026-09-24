package org.manu.hs.application.content.exception;

import org.manu.hs.application.common.exception.base.ValidationException;

public class InvalidConceptTagException extends ValidationException {
    public InvalidConceptTagException(String tag) {
        super("Concept tag '%s' must be kebab-case".formatted(tag));
    }
}