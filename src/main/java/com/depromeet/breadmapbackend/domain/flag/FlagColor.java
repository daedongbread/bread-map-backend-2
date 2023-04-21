package com.depromeet.breadmapbackend.domain.flag;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum FlagColor {
    ORANGE("ORANGE"), // 주황색
    GREEN("GREEN"), // 주황색
    YELLOW("YELLOW"), // 주황색
    CYAN("CYAN"), // 주황색
    BLUE("BLUE"), // 주황색
    SKY("SKY"), // 주황색
    NAVY("NAVY"), // 주황색
    PURPLE("PURPLE"), // 주황색
    RED("RED"), // 주황색
    PINK("PINK"), // 주황색
    GRAY("GRAY"), // 주황색
    ;

    private final String code;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static FlagColor findByCode(String code) {
        return Stream.of(FlagColor.values())
                .filter(c -> c.code.equalsIgnoreCase(code)) // 대소문자로 받음
                .findFirst()
                .orElse(null);
    }
}
