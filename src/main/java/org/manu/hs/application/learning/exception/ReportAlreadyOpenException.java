package org.manu.hs.application.learning.exception;

import org.manu.hs.application.common.exception.base.ConflictException;

/** The learner already has an open report on this question; one per learner until it is decided. */
public class ReportAlreadyOpenException extends ConflictException {
    public ReportAlreadyOpenException(Long questionId) {
        super("You already reported question %d".formatted(questionId));
    }
}
