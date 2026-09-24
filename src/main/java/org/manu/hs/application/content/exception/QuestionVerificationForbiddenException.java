package org.manu.hs.application.content.exception;

import org.manu.hs.application.common.exception.base.ForbiddenException;

/** Verifying is for whoever curates the question: staff on a featured concept, otherwise its concepts' owners. */
public class QuestionVerificationForbiddenException extends ForbiddenException {
    public QuestionVerificationForbiddenException(Long questionId) {
        super("Only whoever curates question %d may verify it".formatted(questionId));
    }
}
