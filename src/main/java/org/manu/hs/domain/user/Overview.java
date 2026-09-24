package org.manu.hs.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * The numbers at the top of the admin page: the people using the platform, this week's activity
 * (the last 7 days), and what waits for a decision.
 */
@AllArgsConstructor
@Builder
@Getter
public class Overview {

    /** Accounts that aren't deleted, without the house account. */
    private int users;
    private int newUsersThisWeek;
    /** Users who answered at least once this week. */
    private int activeUsersThisWeek;
    private int answersThisWeek;
    /** Open reports, praise included. */
    private int openReports;
    /** Submitted questions waiting for a concept owner's review, each counted once. */
    private int questionsAwaitingReview;
}
