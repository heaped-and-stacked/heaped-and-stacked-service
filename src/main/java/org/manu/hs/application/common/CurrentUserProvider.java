package org.manu.hs.application.common;

import org.manu.hs.application.common.exception.AdminRequiredException;
import org.manu.hs.application.common.exception.StaffRequiredException;

/**
 * Who is calling. The id is null for a visitor who hasn't signed in.
 * <p>
 * Content rules ask isStaff (curating everyone's concepts and questions); user management asks
 * isAdmin. Admins are staff too.
 */
public interface CurrentUserProvider {
    Long userId();

    boolean isAdmin();

    boolean isStaff();

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

    /**
     * Refuses the operation unless the caller is staff (or an admin).
     *
     * @param operation what is being attempted, for the message, e.g. "retire a concept"
     */
    default void requireStaff(String operation) {
        if (!isStaff()) {
            throw new StaffRequiredException(operation);
        }
    }
}
