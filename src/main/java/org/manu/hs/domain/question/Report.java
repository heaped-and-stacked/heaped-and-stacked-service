package org.manu.hs.domain.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * A learner's report that a question is wrong or unsuitable, and what became of it.
 */
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
public class Report {

    private Long id;
    private Question question;
    private Long reportedBy;
    private ReportReason reason;
    /** What's wrong, in the learner's words. Optional. */
    private String note;
    private ReportStatus status;
    /** Who resolved or dismissed it, and when; empty while it's open. */
    private Long resolvedBy;
    private Instant resolvedAt;
    private Instant reportedAt;

}
