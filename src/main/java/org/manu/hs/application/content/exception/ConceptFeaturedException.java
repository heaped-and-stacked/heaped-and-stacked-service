package org.manu.hs.application.content.exception;

import org.manu.hs.application.common.exception.base.ConflictException;

/** A featured concept stays published: it's unfeatured first, so the gym's pool never shrinks by accident. */
public class ConceptFeaturedException extends ConflictException {
    public ConceptFeaturedException(String tag) {
        super("Concept '%s' is featured: unfeature it first".formatted(tag));
    }
}
