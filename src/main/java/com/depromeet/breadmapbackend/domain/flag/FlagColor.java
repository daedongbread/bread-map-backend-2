package com.depromeet.breadmapbackend.domain.flag;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FlagColor {
    ORANGE("주황색"),
    GREEN("초록색"),
    YELLOW("노란색"),
    CYAN("청록색"),
    BLUE("초록색"),
    SKY("하늘색"),
    NAVY("네이비색"),
    PURPLE("보라색"),
    RED("빨간색"),
    PINK("핑크색"),
    ;

    private final String code;
}
