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
@ConfigurationProperties(prefix = "firebase")
public class CustomFirebaseProperties {
    @NotBlank(message = "해당값은 필수 값입니다")
    private final String url;
    @NotBlank(message = "해당값은 필수 값입니다")
    private final String path;
    @NotBlank(message = "해당값은 필수 값입니다")
    private final String scope;
    @NotBlank(message = "해당값은 필수 값입니다")
    private final String projectId;
    @NotNull(message = "해당값은 필수 값입니다")
    private final Message message;

    @Getter
    @RequiredArgsConstructor
    public static class Message {
        @NotNull(message = "해당값은 필수 값입니다")
        private final Path path;

        @Getter
        @RequiredArgsConstructor
        public static class Path {
            @NotBlank(message = "해당값은 필수 값입니다")
            private final String user;
            @NotBlank(message = "해당값은 필수 값입니다")
            private final String review;
        }
    }
}
