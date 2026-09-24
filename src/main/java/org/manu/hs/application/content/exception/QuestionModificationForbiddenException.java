package org.manu.hs.application.content.exception;

import org.manu.hs.application.common.exception.base.ForbiddenException;

public class QuestionModificationForbiddenException extends ForbiddenException {
    public QuestionModificationForbiddenException(Long id) {
        super("Only the author may modify question '%d', and only while it is a draft".formatted(id));
    }
}
