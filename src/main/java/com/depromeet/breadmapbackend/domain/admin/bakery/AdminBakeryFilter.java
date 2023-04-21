package com.depromeet.breadmapbackend.domain.admin.bakery;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum AdminBakeryFilter {
    BAKERY_REPORT_IMAGE("BAKERY_REPORT_IMAGE"), // 대표 이미지
    PRODUCT_ADD_REPORT("PRODUCT_ADD_REPORT"), // 메뉴 제보
    BAKERY_UPDATE_REPORT("BAKERY_UPDATE_REPORT"), // 정보 수정
    NEW_REVIEW("NEW_REVIEW") // 신규 리뷰
    ;

    private final String code;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static AdminBakeryFilter findByCode(String code) {
        return Stream.of(AdminBakeryFilter.values())
                .filter(c -> c.code.equalsIgnoreCase(code)) // 대소문자로 받음
                .findFirst()
                .orElse(null);
    }
}
