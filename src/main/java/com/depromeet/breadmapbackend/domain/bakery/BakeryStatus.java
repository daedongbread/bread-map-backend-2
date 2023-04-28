package com.depromeet.breadmapbackend.domain.bakery;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum BakeryStatus {
    POSTING("POSTING"), // 게시중
    UNPOSTING("UNPOSTING") // 미게시
    ;

    private final String code;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static BakeryStatus findByCode(String code) {
        return Stream.of(BakeryStatus.values())
                .filter(c -> c.code.equalsIgnoreCase(code)) // 대소문자로 받음
                .findFirst()
                .orElse(null);
    }
}
