package org.manu.hs.application.learning.exception;

import org.manu.hs.application.common.exception.base.NotFoundException;

/**
 * The question doesn't exist, or learners can't answer it: not submitted, or not accepted
 * for any published concept yet. A 404 either way, so nothing is revealed about it.
 */
public class QuestionNotAvailableException extends NotFoundException {
    public QuestionNotAvailableException(Long questionId) {
        super("Question %d is not available for learning".formatted(questionId));
    }
}
