package org.manu.hs.application.content.exception;

import org.manu.hs.application.common.exception.base.NotFoundException;

public class ConceptNotFoundException extends NotFoundException {
    public ConceptNotFoundException(String tag) {
        super("Concept with tag '%s' not found".formatted(tag));
    }
}
