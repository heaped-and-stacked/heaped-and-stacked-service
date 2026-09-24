package org.manu.hs.application.user.exception;

import org.manu.hs.application.common.exception.base.ConflictException;

/**
 * Refuses a change to the house account: it owns the house content (the content migration
 * creates it), and nobody signs in as it, so deleting, blocking or demoting it can only do harm.
 */
public class HouseAccountException extends ConflictException {
    public HouseAccountException(Long userId) {
        super("User '%d' is the house account, which owns the featured content: it can't be deleted, blocked or have its role changed".formatted(userId));
    }
}
