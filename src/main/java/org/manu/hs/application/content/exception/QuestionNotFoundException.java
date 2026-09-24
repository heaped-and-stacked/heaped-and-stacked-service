package org.manu.hs.application.content.exception;

import org.manu.hs.application.common.exception.base.NotFoundException;

public class QuestionNotFoundException extends NotFoundException {
    public QuestionNotFoundException(Long id) {
        super("Question with id '%d' not found".formatted(id));
    }
}
