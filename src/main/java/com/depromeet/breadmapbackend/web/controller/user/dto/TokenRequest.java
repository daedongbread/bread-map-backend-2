package com.depromeet.breadmapbackend.web.controller.user.dto;

import com.depromeet.breadmapbackend.web.advice.ValidationGroups.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class TokenRequest {
    @NotBlank(message = "Access Token 은 필수 값입니다.", groups = NotEmptyGroup.class)
    private String accessToken;
    @NotBlank(message = "Refresh Token 은 필수 값입니다.", groups = NotEmptyGroup.class)
    private String refreshToken;

    @Builder
    public TokenRequest(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}