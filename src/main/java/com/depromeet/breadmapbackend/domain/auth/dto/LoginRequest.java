package com.depromeet.breadmapbackend.domain.auth.dto;

import javax.validation.constraints.NotBlank;

import com.depromeet.breadmapbackend.global.annotation.EnumCheck;
import com.depromeet.breadmapbackend.global.exception.ValidationGroups;
import com.depromeet.breadmapbackend.global.security.domain.OAuthType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
	@EnumCheck(groups = ValidationGroups.PatternCheckGroup.class)
	private OAuthType type;
	@NotBlank(message = "ID Token은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
	private String idToken;
	private String deviceToken;

}
