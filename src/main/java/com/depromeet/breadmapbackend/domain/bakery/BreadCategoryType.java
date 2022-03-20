package com.depromeet.breadmapbackend.domain.bakery;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum BreadCategoryType {

    식사빵("식사빵"),
    구움과자류("구움과자류"),
    마카롱("마카롱"),
    케이크("케이크"),
    크림빵("크림빵"),
    도넛("도넛"),
    추억의빵("추억의 빵"),
    과자류("과자류"),
    크로와상("크로와상"),
    쿠키("쿠키"),
    파이디저트("파이/디저트"),
    비건키토("비건/키토"),
    기타("기타");

    private final String code;

}
