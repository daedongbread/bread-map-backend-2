package com.depromeet.breadmapbackend.domain.admin.bakery;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum AdminBakeryImageType {
    BAKERY_REPORT_IMAGE("BAKERY_REPORT_IMAGE"), // 대표이미지
    PRODUCT_ADD_REPORT_IMAGE("PRODUCT_ADD_REPORT_IMAGE"), // 메뉴제보이미지
    REVIEW_IMAGE("REVIEW_IMAGE") // 리뷰이미지
    ;

    private final String code;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static AdminBakeryImageType findByCode(String code) {
        return Stream.of(AdminBakeryImageType.values())
                .filter(c -> c.code.equalsIgnoreCase(code)) // 대소문자로 받음
                .findFirst()
                .orElse(null);
    }
}
