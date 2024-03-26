package com.depromeet.breadmapbackend.domain.review.tag;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewTagType {
    BREAD("빵"),
    BAKERY("빵집")
    ;

    private final String description;

}
