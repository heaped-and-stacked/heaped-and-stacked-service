package org.manu.hs.application.content.exception;

import org.manu.hs.application.common.exception.base.ForbiddenException;

public class ConceptModificationForbiddenException extends ForbiddenException {
    public ConceptModificationForbiddenException(String tag) {
        super("Only the owner of concept '%s' may change it".formatted(tag));
    }
}
