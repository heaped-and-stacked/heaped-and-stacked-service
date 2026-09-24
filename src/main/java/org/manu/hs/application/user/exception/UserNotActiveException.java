package org.manu.hs.application.user.exception;

import org.manu.hs.application.common.exception.base.ForbiddenException;
import org.manu.hs.domain.user.UserStatus;

/**
 * A blocked or deleted account tried to sign in.
 */
public class UserNotActiveException extends ForbiddenException {
    public UserNotActiveException(Long userId, UserStatus status) {
        super("User '%d' can't sign in: the account is %s".formatted(userId, status.name().toLowerCase()));
    }
}
