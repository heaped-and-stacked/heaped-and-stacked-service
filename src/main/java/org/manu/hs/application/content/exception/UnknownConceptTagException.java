package org.manu.hs.application.content.exception;

import org.manu.hs.application.common.exception.base.ValidationException;

/**
 * A question payload references a concept that doesn't exist. This is a 400 (bad
 * payload), not a 404: the question endpoint exists, the input is what's wrong.
 */
public class UnknownConceptTagException extends ValidationException {
    public UnknownConceptTagException(String tag) {
        super("Concept tag '%s' does not exist".formatted(tag));
    }
}
