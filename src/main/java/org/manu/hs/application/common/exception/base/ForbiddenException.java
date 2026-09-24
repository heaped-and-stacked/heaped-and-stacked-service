package org.manu.hs.application.common.exception.base;

public abstract class ForbiddenException extends RuntimeException {
    protected ForbiddenException(String message) {
        super(message);
    }
}
