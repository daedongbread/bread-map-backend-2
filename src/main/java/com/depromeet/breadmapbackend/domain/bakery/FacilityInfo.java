package com.depromeet.breadmapbackend.domain.bakery;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum FacilityInfo {
    PARKING("PARKING"), // 주차가능
    WIFI("WIFI"), //와이파이
    DELIVERY("DELIVERY"), // 배달
    PET("PET"), // 반려동물
    SHIPPING("SHIPPING"), // 택배
    BOOKING("BOOKING") // 예약
    ;

    private final String code;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static FacilityInfo findByCode(String code) {
        return Stream.of(FacilityInfo.values())
                .filter(c -> c.code.equalsIgnoreCase(code)) // 대소문자로 받음
                .findFirst()
                .orElse(null);
    }
}
