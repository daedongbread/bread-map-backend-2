package com.depromeet.breadmapbackend.global.security.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum OAuthType {

    GOOGLE("GOOGLE"), // 구글
    KAKAO("KAKAO"), // 카카오
    APPLE("APPLE") // 애플
    ;

    private final String code;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static OAuthType findByCode(String code) {
        return Stream.of(OAuthType.values())
                .filter(c -> c.code.equalsIgnoreCase(code)) // 대소문자로 받음
                .findFirst()
                .orElse(null);
    }
}
