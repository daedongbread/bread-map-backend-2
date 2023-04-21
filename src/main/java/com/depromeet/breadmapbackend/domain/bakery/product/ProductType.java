package com.depromeet.breadmapbackend.domain.bakery.product;

import com.depromeet.breadmapbackend.domain.admin.bakery.AdminBakeryFilter;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum ProductType {
    BREAD("BREAD", 1), // 빵
    BEVERAGE("BEVERAGE", 2), // 음료
    ETC("ETC", 3); // 기타

    private final String code;
    private final Integer priority;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ProductType findByCode(String code) {
        return Stream.of(ProductType.values())
                .filter(c -> c.code.equalsIgnoreCase(code)) // 대소문자로 받음
                .findFirst()
                .orElse(null);
    }
}
