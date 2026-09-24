package org.manu.hs.application.content.exception;

import org.manu.hs.application.common.exception.base.ConflictException;

/**
 * A question learners have answered may not be deleted: that would erase their history.
 * Retiring it takes it out of learning and keeps the answers.
 */
public class QuestionAnsweredException extends ConflictException {
    public QuestionAnsweredException(Long id) {
        super("Question with id '%d' has answers: retire it instead".formatted(id));
    }
}
