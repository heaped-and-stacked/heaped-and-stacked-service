package org.manu.hs.application.content.exception;

import org.manu.hs.application.common.exception.base.ForbiddenException;

public class QuestionModificationForbiddenException extends ForbiddenException {
    public QuestionModificationForbiddenException(Long id) {
        super("Only the author of question '%d' may change it".formatted(id));
    }
}
