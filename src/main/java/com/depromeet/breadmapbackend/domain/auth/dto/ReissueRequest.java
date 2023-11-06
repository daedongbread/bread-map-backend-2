package com.depromeet.breadmapbackend.domain.auth.dto;

import javax.validation.constraints.NotBlank;

import com.depromeet.breadmapbackend.global.exception.ValidationGroups.NotEmptyGroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReissueRequest {
	@NotBlank(message = "Access Token 은 필수 값입니다.", groups = NotEmptyGroup.class)
	private String accessToken;
	@NotBlank(message = "Refresh Token 은 필수 값입니다.", groups = NotEmptyGroup.class)
	private String refreshToken;
	private String deviceToken;
}