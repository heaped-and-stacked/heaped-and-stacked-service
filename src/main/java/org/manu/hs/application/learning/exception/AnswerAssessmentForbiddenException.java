package org.manu.hs.application.learning.exception;

import org.manu.hs.application.common.exception.base.ForbiddenException;

/** A self-assessment belongs to whoever gave the answer; nobody else may set it, not even admins. */
public class AnswerAssessmentForbiddenException extends ForbiddenException {
    public AnswerAssessmentForbiddenException(Long answerId) {
        super("Only the person who gave answer '%d' may assess it".formatted(answerId));
    }
}
