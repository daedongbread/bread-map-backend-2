package com.depromeet.breadmapbackend.domain.bakery;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum BakerySortType {
    DISTANCE("distance"), // 거리 순
    POPULAR("popular") // 인기 순
    ;

    private final String code;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static BakerySortType findByCode(String code) {
        return Stream.of(BakerySortType.values())
                .filter(c -> c.code.equals(code)) // 소문자로 받음
                .findFirst()
                .orElse(null);
    }
}
