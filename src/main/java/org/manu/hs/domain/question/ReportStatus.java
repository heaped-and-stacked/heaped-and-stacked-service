package org.manu.hs.domain.question;

/**
 * Where a report stands. OPEN: waiting for the question's author, one of its concepts' owners or
 * an admin. RESOLVED: acted on (the question was fixed, benched or deleted). DISMISSED: looked at
 * and found not to need a change.
 */
public enum ReportStatus {
    OPEN,
    RESOLVED,
    DISMISSED
}
