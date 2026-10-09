package org.manu.hs.application.content.exception;

import org.manu.hs.application.common.exception.base.ForbiddenException;

/**
 * Members may not add concepts or questions while contributions are open to staff only
 * (app.content.members-contribute=false). Changing what they already wrote stays allowed.
 */
public class ContributionsClosedException extends ForbiddenException {
    public ContributionsClosedException() {
        super("Only staff may add concepts and questions for now");
    }
}
