package org.manu.hs.api.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.manu.hs.domain.user.Overview;

/** The numbers at the top of the admin page; see Overview. */
@Getter
@AllArgsConstructor
@Builder
public class OverviewResponse {

    private int users;
    private int newUsersThisWeek;
    private int activeUsersThisWeek;
    private int answersThisWeek;
    private int openReports;
    private int questionsAwaitingReview;

    public static OverviewResponse toDto(Overview overview) {
        return OverviewResponse.builder()
                .users(overview.getUsers())
                .newUsersThisWeek(overview.getNewUsersThisWeek())
                .activeUsersThisWeek(overview.getActiveUsersThisWeek())
                .answersThisWeek(overview.getAnswersThisWeek())
                .openReports(overview.getOpenReports())
                .questionsAwaitingReview(overview.getQuestionsAwaitingReview())
                .build();
    }
}
