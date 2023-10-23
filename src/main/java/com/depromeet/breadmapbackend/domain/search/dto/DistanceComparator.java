package com.depromeet.breadmapbackend.domain.search.dto;

import java.util.Comparator;

public class DistanceComparator implements Comparator<SearchEngineDto> {
    @Override
    public int compare(SearchEngineDto dto1, SearchEngineDto dto2) {
        return Double.compare(dto1.getDistance(), dto2.getDistance());
    }
}
