package org.manu.hs.application.user.exception;

import org.manu.hs.application.common.exception.base.ForbiddenException;

/**
 * Sign-in is invite-only (there are invites) and this account has none.
 */
public class SignInNotInvitedException extends ForbiddenException {
    public SignInNotInvitedException(String login) {
        super("GitHub account '%s' isn't invited: sign-in is invite-only".formatted(login));
    }
}
