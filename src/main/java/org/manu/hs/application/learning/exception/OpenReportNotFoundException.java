package org.manu.hs.application.learning.exception;

import org.manu.hs.application.common.exception.base.NotFoundException;

/**
 * The learner has no open report (feedback) on this question to change or withdraw: they never
 * sent one, or it was already decided.
 */
public class OpenReportNotFoundException extends NotFoundException {
    public OpenReportNotFoundException(Long questionId) {
        super("You have no open feedback on question %d".formatted(questionId));
    }
}
