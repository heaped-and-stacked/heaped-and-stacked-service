package org.manu.hs.application.common.exception;

import org.manu.hs.application.common.exception.base.ValidationException;

public class RequiredFieldMissingException extends ValidationException {
    public RequiredFieldMissingException(String fieldName) {
        super(fieldName + " is required");
    }
}