package org.manu.hs.application.common;

import org.manu.hs.application.common.exception.AdminRequiredException;

/**
 * Who is calling. The id is null for a visitor who hasn't signed in.
 */
public interface CurrentUserProvider {
    Long userId();

    boolean isAdmin();

    /**
     * Refuses the operation unless the caller is an admin.
     *
     * @param operation what is being attempted, for the message, e.g. "list users"
     */
    default void requireAdmin(String operation) {
        if (!isAdmin()) {
            throw new AdminRequiredException(operation);
        }
    }
}
