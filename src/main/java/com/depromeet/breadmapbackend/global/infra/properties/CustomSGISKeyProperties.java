package com.depromeet.breadmapbackend.global.infra.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Validated
@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "sgis")
public class CustomSGISKeyProperties {
    @NotNull(message = "해당값은 필수 값입니다")
    private final Integer src;
    @NotNull(message = "해당값은 필수 값입니다")
    private final Integer dst1;
    @NotNull(message = "해당값은 필수 값입니다")
    private final Integer dst2;
    @NotBlank(message = "해당값은 필수 값입니다")
    private final String key;
    @NotBlank(message = "해당값은 필수 값입니다")
    private final String secret;
}
