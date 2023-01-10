package com.depromeet.breadmapbackend.security.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProviderType {

    GOOGLE("구글"),
    KAKAO("카카오"),
    APPLE("애플");

    private final String code;

}
