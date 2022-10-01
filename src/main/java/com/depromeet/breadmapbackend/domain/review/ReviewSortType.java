package com.depromeet.breadmapbackend.domain.review;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewSortType {
    LATEST("최신순"),
    HIGH("별점 높은 순"),
    LOW("별점 낮은 순");

    private final String code;
}
