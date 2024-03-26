package com.depromeet.breadmapbackend.domain.review.tag;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BakeryTagType {
    // 화면 노출되는 데이터는 enum descrioption이 아닌 db 저장된 데이터를 보여줌
    GOOD_FOR_DATING("데이트하기 좋아요"),
    GOOD_VIEW("뷰 맛집이에요"),
    COZY_SPACE("아늑해요"),
    GOOD_FOR_MEETING("모임하기 좋아요"),
    SPECIAL_CONCEPT("컨셉이 독특해요")
    ;

    private final String description;

}
