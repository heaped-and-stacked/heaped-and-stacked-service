package org.manu.hs.application.content.exception;

import org.manu.hs.application.common.exception.base.ForbiddenException;

/**
 * Members may not add questions (or concepts) while that's open to staff only
 * (app.content.members-add-questions / members-add-concepts). Changing what they already wrote stays
 * allowed. `what`: "questions" or "concepts".
 */
public class ContributionsClosedException extends ForbiddenException {
    public ContributionsClosedException(String what) {
        super("Only staff may add %s for now".formatted(what));
    }
}
