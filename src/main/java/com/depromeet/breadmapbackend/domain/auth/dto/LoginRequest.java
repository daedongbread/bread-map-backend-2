package com.depromeet.breadmapbackend.domain.auth.dto;

import com.depromeet.breadmapbackend.global.exception.ValidationGroups;
import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private OAuthType type; // TODO
    @NotBlank(message = "ID Token은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private String idToken;
}
