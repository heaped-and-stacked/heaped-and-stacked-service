package org.manu.hs.api.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.manu.hs.domain.question.Report;
import org.manu.hs.domain.question.ReportReason;
import org.manu.hs.domain.question.ReportStatus;

import java.time.Instant;

/**
 * The learner's own open feedback on a question, so they can see, change or withdraw it. The
 * question itself is left out: the learner already has it, and its model answer stays with the
 * learning flow.
 */
@Getter
@AllArgsConstructor
@Builder
public class MyReportResponse {

    private Long id;
    private ReportReason reason;
    private String note;
    private ReportStatus status;
    private Instant reportedAt;

    public static MyReportResponse toDto(Report report) {
        return MyReportResponse.builder()
                .id(report.getId())
                .reason(report.getReason())
                .note(report.getNote())
                .status(report.getStatus())
                .reportedAt(report.getReportedAt())
                .build();
    }
}
