package org.manu.hs.api.content.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/** One page of the questions waiting for approval, oldest first, with the totals across every page. */
@Getter
@AllArgsConstructor
@Builder
public class ReviewPageResponse {

    private List<ReviewResponse> items;
    /** Zero-based. */
    private int page;
    private int size;
    private long totalItems;
    private int totalPages;
}
