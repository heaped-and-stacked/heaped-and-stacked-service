package org.manu.hs.application.content.exception;

import org.manu.hs.application.common.exception.base.ForbiddenException;

public class ConceptModificationForbiddenException extends ForbiddenException {
    public ConceptModificationForbiddenException(String tag) {
        super("Only the author may modify concept '%s', and only while it is a draft".formatted(tag));
    }
}
