package org.manu.hs.application.content.exception;

import org.manu.hs.application.common.exception.base.ConflictException;

/** A question is verified once a concept accepted it. */
public class QuestionNotAcceptedException extends ConflictException {
    public QuestionNotAcceptedException(Long questionId) {
        super("Question %d isn't accepted for any concept yet, so it can't be verified".formatted(questionId));
    }
}
