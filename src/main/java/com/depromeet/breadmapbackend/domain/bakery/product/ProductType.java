package com.depromeet.breadmapbackend.domain.bakery.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductType {
    BREAD("빵", 1),
    BEVERAGE("음료", 2),
    ETC("기타", 3);

    private final String code;
    private final Integer priority;
}
