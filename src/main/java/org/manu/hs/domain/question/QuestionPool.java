package org.manu.hs.domain.question;

/** Which of the available questions a learner's next question is drawn from. */
public enum QuestionPool {

    /** Only verified ones (the web app trains ALL, or TO_VERIFY in reviewer mode). */
    VERIFIED,
    /** Every available one, verified or not. */
    ALL,
    /**
     * The unverified ones the learner may verify (staff: on featured concepts; owners: on their
     * own), picked as in any workout.
     */
    TO_VERIFY
}
