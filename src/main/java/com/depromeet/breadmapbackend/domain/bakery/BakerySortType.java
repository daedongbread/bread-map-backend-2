package com.depromeet.breadmapbackend.domain.bakery;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BakerySortType {
    DISTANCE,
    POPULAR;
}
