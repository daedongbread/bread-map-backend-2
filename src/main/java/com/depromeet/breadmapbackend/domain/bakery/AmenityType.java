package com.depromeet.breadmapbackend.domain.bakery;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum AmenityType {

    PET("애완 동물"),
    WIFI("와이파이"),
    PARKING("주차"),
    DELIVERY("배달"),
    SHIPPING("배송");

    private final String code;

    public static AmenityType of(String name) {
        return Arrays.stream(AmenityType.values())
                .filter(amenityType -> amenityType.name().equals(name))
                .findAny()
                .orElse(null);
    }

}
