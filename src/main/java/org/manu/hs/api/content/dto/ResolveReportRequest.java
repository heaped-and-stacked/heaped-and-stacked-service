package org.manu.hs.api.content.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.manu.hs.domain.question.ReportStatus;

/** RESOLVED (the question was fixed) or DISMISSED (nothing to fix). */
@Getter
@Setter
@NoArgsConstructor
public class ResolveReportRequest {
    private ReportStatus status;
}
