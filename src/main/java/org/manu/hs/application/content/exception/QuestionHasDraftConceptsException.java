package org.manu.hs.application.content.exception;

import org.manu.hs.application.common.exception.base.ConflictException;

import java.util.List;

/**
 * A SUBMITTED question may not link a concept that is still a DRAFT: learners would
 * reach it through a topic that hasn't been published. A 409, because the request is
 * valid; it's the current state of the linked concepts that blocks it.
 */
public class QuestionHasDraftConceptsException extends ConflictException {
    public QuestionHasDraftConceptsException(List<String> draftTags) {
        super("A question can't be submitted while these concepts are drafts: %s"
                .formatted(String.join(", ", draftTags)));
    }
}
