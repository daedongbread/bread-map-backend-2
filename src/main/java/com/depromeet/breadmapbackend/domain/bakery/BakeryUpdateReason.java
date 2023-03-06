package com.depromeet.breadmapbackend.domain.bakery;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BakeryUpdateReason {
    BAKERY_SHUTDOWN("빵집 폐업"),
    BAKERY_MOVE("빵집 이동 (주소 이전)"),
    INFORMATION_ERROR("정보 오류 (영업 시간, 전화 번호, 주차 등)"),
    PRODUCT_CHANGE("메뉴 변경 (메뉴명, 가격, 판매 중단 등)"),
    ETC("기타");

    @Getter
    private final String code;
}
