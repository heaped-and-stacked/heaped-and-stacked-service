package org.manu.hs.api.content.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/** One page of the reports, oldest first (open) or newest decision first (decided), with the totals across every page. */
@Getter
@AllArgsConstructor
@Builder
public class ReportPageResponse {

    private List<ReportResponse> items;
    /** Zero-based. */
    private int page;
    private int size;
    private long totalItems;
    private int totalPages;
}
