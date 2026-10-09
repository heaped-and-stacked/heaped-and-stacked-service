package org.manu.hs.domain.user;

/**
 * What a user may do beyond their own content. STAFF curate everyone's concepts and
 * questions (reviews, reports, edits, retiring); ADMIN also manage users.
 */
public enum UserRole {
    MEMBER,
    STAFF,
    ADMIN;

    /** Staff and admins act on every concept and question, not just their own. */
    public boolean curatesAllContent() {
        return this == STAFF || this == ADMIN;
    }
}
