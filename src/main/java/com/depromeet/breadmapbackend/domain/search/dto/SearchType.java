package com.depromeet.breadmapbackend.domain.search.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchType {
    DISTANCE,
    POPULAR;
}
