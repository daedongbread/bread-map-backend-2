package com.depromeet.breadmapbackend.domain.bakery;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum FacilityInfo {
    PARKING("주차 가능"),
    WIFI("와이파이"),
    DELIVERY("배달"),
    PET("반려동물"),
    SHIPPING("택배"),
    BOOKING("예약");

    private final String code;
}
