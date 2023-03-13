package com.depromeet.breadmapbackend.web.controller.user.dto;

import com.depromeet.breadmapbackend.web.advice.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequest {
    @NotBlank(message = "Access Token 은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private String accessToken;
    @NotBlank(message = "Refresh Token 은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private String refreshToken;
    @NotBlank(message = "Device Token 은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private String deviceToken;
}
