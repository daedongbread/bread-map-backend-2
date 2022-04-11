package com.depromeet.breadmapbackend.domain.bakery;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum FacilityInfo {

    PET("애완 동물"),
    WIFI("와이파이"),
    PARKING("주차"),
    DELIVERY("배달"),
    SHIPPING("배송");

    private final String code;
}
