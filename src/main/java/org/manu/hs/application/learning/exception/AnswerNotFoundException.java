package org.manu.hs.application.learning.exception;

import org.manu.hs.application.common.exception.base.NotFoundException;

public class AnswerNotFoundException extends NotFoundException {
    public AnswerNotFoundException(Long id) {
        super("Answer with id '%d' not found".formatted(id));
    }
}
