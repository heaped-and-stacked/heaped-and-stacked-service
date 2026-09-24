package org.manu.hs.application.common.exception;

import org.manu.hs.application.common.exception.base.ForbiddenException;

public class AdminRequiredException extends ForbiddenException {
    public AdminRequiredException(String operation) {
        super("Only an admin may %s".formatted(operation));
    }
}
