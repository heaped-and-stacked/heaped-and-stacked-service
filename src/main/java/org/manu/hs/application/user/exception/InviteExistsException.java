package org.manu.hs.application.user.exception;

import org.manu.hs.application.common.exception.base.ConflictException;

/**
 * The username is already invited, whatever its case.
 */
public class InviteExistsException extends ConflictException {
    public InviteExistsException(String login) {
        super("GitHub account '%s' is already invited".formatted(login));
    }
}
