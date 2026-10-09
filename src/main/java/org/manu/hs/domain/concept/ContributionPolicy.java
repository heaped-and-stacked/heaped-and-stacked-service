package org.manu.hs.domain.concept;

/**
 * What happens when someone other than the owner links a question to this concept.
 * AUTO_ACCEPT: the question is accepted for the concept straight away.
 * OWNER_APPROVAL: the question waits until the concept's owner (or staff, admins included) accepts it.
 */
public enum ContributionPolicy {
    AUTO_ACCEPT,
    OWNER_APPROVAL
}
