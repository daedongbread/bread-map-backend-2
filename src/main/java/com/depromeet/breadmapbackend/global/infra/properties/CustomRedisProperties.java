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
@ConfigurationProperties(prefix = "spring.redis")
public class CustomRedisProperties {
    @NotBlank(message = "해당값은 필수 값입니다")
    private final String host;
    @NotNull(message = "해당값은 필수 값입니다")
    private final Integer port;
    @NotNull(message = "해당값은 필수 값입니다")
    private final Key key;

    @Getter
    @RequiredArgsConstructor
    public static class Key {
        @NotBlank(message = "해당값은 필수 값입니다")
        private final String delete;
        @NotBlank(message = "해당값은 필수 값입니다")
        private final String recent;
        @NotBlank(message = "해당값은 필수 값입니다")
        private final String access;
        @NotBlank(message = "해당값은 필수 값입니다")
        private final String refresh;
        @NotBlank(message = "해당값은 필수 값입니다")
        private final String adminRefresh;
        @NotBlank(message = "해당값은 필수 값입니다")
        private final String bakeryReview;
        @NotBlank(message = "해당값은 필수 값입니다")
        private final String productReview;
        @NotBlank(message = "해당값은 필수 값입니다")
        private final String userReview;
    }
}
