package com.depromeet.breadmapbackend.domain.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductType {
    BREAD("빵"),
    BEVERAGE("음료"),
    ETC("기타");

    private final String code;
}
