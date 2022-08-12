package com.depromeet.breadmapbackend.domain.bakery;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BakeryStatus {
    posting("게시중"),
    unposting("미게시");

    private final String code;
}
