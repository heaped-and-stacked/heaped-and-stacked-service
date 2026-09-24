package org.manu.hs.api.content.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/** One page of the questions a user curates, with the totals across every page. */
@Getter
@AllArgsConstructor
@Builder
public class QuestionPageResponse {

    private List<QuestionResponse> items;
    /** Zero-based. */
    private int page;
    private int size;
    private long totalItems;
    private int totalPages;
}
