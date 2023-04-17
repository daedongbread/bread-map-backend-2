package com.depromeet.breadmapbackend.domain.admin.bakery;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdminBakeryFilter {
    bakery_report_image("대표 이미지"),
    product_add_report("메뉴 제보"),
    bakery_update_report("정보 수정"),
    new_review("신규 리뷰");

    private final String code;
}
