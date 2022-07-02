package com.depromeet.breadmapbackend.domain.review;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewSortType {
    latest,
    high,
    low
}
