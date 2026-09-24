package org.manu.hs.application.common.exception;

import org.manu.hs.application.common.exception.base.ForbiddenException;

/** Curating content beyond your own is for staff (and admins, who are staff too). */
public class StaffRequiredException extends ForbiddenException {
    public StaffRequiredException(String operation) {
        super("Only staff may %s".formatted(operation));
    }
}
