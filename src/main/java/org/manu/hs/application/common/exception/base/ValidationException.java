package org.manu.hs.application.common.exception.base;

public abstract class ValidationException extends RuntimeException {
    protected ValidationException(String message) {
        super(message);
    }
}