package org.manu.hs.application.content.exception;

import org.manu.hs.application.common.exception.base.ConflictException;

/** Only a published (ACTIVE) concept can be featured. */
public class ConceptNotPublishedException extends ConflictException {
    public ConceptNotPublishedException(String tag) {
        super("Concept '%s' isn't published, so it can't be featured".formatted(tag));
    }
}
