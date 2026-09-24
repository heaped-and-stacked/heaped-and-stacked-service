package org.manu.hs.application.content.exception;

import org.manu.hs.application.common.exception.base.ConflictException;

/**
 * The report was already resolved or dismissed: a decision is final, so a second one (from
 * someone else, or racing the first) is refused instead of overwriting it.
 */
public class ReportAlreadyDecidedException extends ConflictException {
    public ReportAlreadyDecidedException(Long reportId) {
        super("Report '%d' was already decided".formatted(reportId));
    }
}
