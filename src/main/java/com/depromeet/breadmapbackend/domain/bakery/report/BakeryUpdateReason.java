package com.depromeet.breadmapbackend.domain.bakery.report;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum BakeryUpdateReason {
    BAKERY_SHUTDOWN("BAKERY_SHUTDOWN"), // 빵집 폐업
    BAKERY_MOVE("BAKERY_MOVE"), // 빵집 이동 (주소 이전)
    INFORMATION_ERROR("INFORMATION_ERROR"), // 정보 오류 (영업 시간, 전화 번호, 주차 등)
    PRODUCT_CHANGE("PRODUCT_CHANGE"), // 메뉴 변경 (메뉴명, 가격, 판매 중단 등)
    ETC("ETC") // 기타
    ;

    private final String code;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static BakeryUpdateReason findByCode(String code) {
        return Stream.of(BakeryUpdateReason.values())
                .filter(c -> c.code.equalsIgnoreCase(code)) // 대소문자로 받음
                .findFirst()
                .orElse(null);
    }
}
