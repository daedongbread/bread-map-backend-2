package com.depromeet.breadmapbackend.domain.search.dto;

import java.util.Comparator;

public class ReviewComparator implements Comparator<SearchEngineDto> {
    @Override
    public int compare(SearchEngineDto dto1, SearchEngineDto dto2) {
        return Double.compare(dto2.getReviewNum(), dto1.getReviewNum());
    }
}
