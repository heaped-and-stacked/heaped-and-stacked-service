package org.manu.hs.application.user.exception;

import org.manu.hs.application.common.exception.base.ConflictException;

/** Admins are set in the configuration (BOOTSTRAP_ADMIN_IDS): their role isn't changed in the app. */
public class AdminFromConfigurationException extends ConflictException {
    public AdminFromConfigurationException(Long userId) {
        super("User '%d' is an admin: admins are set in the configuration, not in the app".formatted(userId));
    }
}
