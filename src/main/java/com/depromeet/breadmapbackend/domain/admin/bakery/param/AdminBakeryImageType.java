package com.depromeet.breadmapbackend.domain.admin.bakery.param;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum AdminBakeryImageType {
    BAKERY_REPORT_IMAGE("bakery-report-image"), // 대표이미지
    PRODUCT_ADD_REPORT_IMAGE("product-add-report-image"), // 메뉴제보이미지
    REVIEW_IMAGE("review-image") // 리뷰이미지
    ;

    private final String code;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static AdminBakeryImageType findByCode(String code) {
        return Stream.of(AdminBakeryImageType.values())
                .filter(c -> c.code.equals(code)) // 소문자로 받음
                .findFirst()
                .orElse(null);
    }
}
