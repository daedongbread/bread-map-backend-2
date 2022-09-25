package com.depromeet.breadmapbackend.domain.bakery;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BakeryStatus {
    POSTING("게시중"),
    UNPOSTING("미게시");

    private final String code;
}
