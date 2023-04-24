package com.depromeet.breadmapbackend.domain.admin.bakery.param;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum AdminBakeryFilter {
    BAKERY_REPORT_IMAGE("bakery-report-image"), // 대표 이미지
    PRODUCT_ADD_REPORT("product-add-report"), // 메뉴 제보
    BAKERY_UPDATE_REPORT("bakery-update-report"), // 정보 수정
    NEW_REVIEW("new-review") // 신규 리뷰
    ;

    private final String code;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static AdminBakeryFilter findByCode(String code) {
        return Stream.of(AdminBakeryFilter.values())
                .filter(c -> c.code.equals(code)) // 소문자로 받음
                .findFirst()
                .orElse(null);
    }
}
