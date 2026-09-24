package org.manu.hs.application.user.exception;

import org.manu.hs.application.common.exception.base.ConflictException;

/**
 * Refuses a change that would leave the platform without an active admin, so an
 * admin can't lock everyone (including themselves) out of moderation.
 */
public class LastAdminException extends ConflictException {
    public LastAdminException(Long userId) {
        super("User '%d' is the last active admin and can't be demoted, blocked or deleted".formatted(userId));
    }
}
