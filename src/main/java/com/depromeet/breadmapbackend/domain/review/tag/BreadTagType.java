package com.depromeet.breadmapbackend.domain.review.tag;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BreadTagType {
    // 화면 노출되는 데이터는 enum descrioption이 아닌 db 저장된 데이터를 보여줌
    GOOD_BREAD("빵이 맛있어요"),
    GOOD_DRINK("음료가 맛있어요"),
    SPECIAL_BREAD("특별한 빵이 있어요"),
    REASONABLE_PRICE("가성비가 좋아요"),
    GOOD_AMOUNT("양이 많아요")
    ;

    private final String description;

}
