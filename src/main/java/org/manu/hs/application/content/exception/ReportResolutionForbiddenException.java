package org.manu.hs.application.content.exception;

import org.manu.hs.application.common.exception.base.ForbiddenException;

/** Only the question's author, the owner of a concept it is linked to, or an admin decides a report. */
public class ReportResolutionForbiddenException extends ForbiddenException {
    public ReportResolutionForbiddenException(Long id) {
        super("Only the question's author or a concept owner may decide report '%d'".formatted(id));
    }
}
