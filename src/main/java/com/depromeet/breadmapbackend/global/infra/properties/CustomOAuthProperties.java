package com.depromeet.breadmapbackend.global.infra.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Getter
@Validated
@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "oauth")
public class CustomOAuthProperties {
    @NotBlank(message = "해당값은 필수 값입니다")
    private final String google;
    @NotBlank(message = "해당값은 필수 값입니다")
    private final String kakao;
    @NotBlank(message = "해당값은 필수 값입니다")
    private final String apple;
}
