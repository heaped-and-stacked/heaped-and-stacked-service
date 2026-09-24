package org.manu.hs.application.content.exception;

import org.manu.hs.application.common.exception.base.NotFoundException;

public class ConceptLinkNotFoundException extends NotFoundException {
    public ConceptLinkNotFoundException(Long questionId, String conceptTag) {
        super("Question %d is not linked to concept '%s'".formatted(questionId, conceptTag));
    }
}
