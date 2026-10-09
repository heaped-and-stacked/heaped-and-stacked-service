package org.manu.hs.api.content.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.manu.hs.domain.question.Report;
import org.manu.hs.domain.question.ReportReason;
import org.manu.hs.domain.question.ReportStatus;

import java.time.Instant;

/**
 * A learner's report on a question, for whoever decides it. Who reported it is left out, so
 * reports can't turn into arguments between people.
 */
@Getter
@AllArgsConstructor
@Builder
public class ReportResponse {

    private Long id;
    private QuestionResponse question;
    private ReportReason reason;
    private String note;
    private ReportStatus status;
    private Instant reportedAt;
    /** When it was resolved or dismissed; null while it's open. */
    private Instant resolvedAt;

    public static ReportResponse toDto(Report report) {
        return ReportResponse.builder()
                .id(report.getId())
                .question(QuestionResponse.toDto(report.getQuestion()))
                .reason(report.getReason())
                .note(report.getNote())
                .status(report.getStatus())
                .reportedAt(report.getReportedAt())
                .resolvedAt(report.getResolvedAt())
                .build();
    }
}
