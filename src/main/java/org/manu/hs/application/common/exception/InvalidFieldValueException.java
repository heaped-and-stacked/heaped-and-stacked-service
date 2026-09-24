package org.manu.hs.application.common.exception;

import org.manu.hs.application.common.exception.base.ValidationException;

/**
 * A field is present but its value breaks a rule, e.g.
 * "difficultyScore must be between 1 and 10".
 */
public class InvalidFieldValueException extends ValidationException {
    public InvalidFieldValueException(String fieldName, String rule) {
        super("%s %s".formatted(fieldName, rule));
    }
}
