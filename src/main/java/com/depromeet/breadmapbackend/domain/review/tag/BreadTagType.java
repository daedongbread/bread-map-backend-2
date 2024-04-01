package com.depromeet.breadmapbackend.domain.review.tag;

import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum BreadTagType {
    // 화면 노출되는 데이터는 enum descrioption이 아닌 db 저장된 데이터를 보여줌
    GOOD_BREAD(1, "빵이 맛있어요"),
    GOOD_DRINK(2, "음료가 맛있어요"),
    SPECIAL_BREAD(3, "특별한 빵이 있어요"),
    REASONABLE_PRICE(4, "가성비가 좋아요"),
    GOOD_AMOUNT(5, "양이 많아요")
    ;

    private final long breadTagId;
    private final String description;

}
