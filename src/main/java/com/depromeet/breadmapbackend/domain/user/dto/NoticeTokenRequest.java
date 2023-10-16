package com.depromeet.breadmapbackend.domain.user.dto;

import javax.validation.constraints.NotNull;

import com.depromeet.breadmapbackend.global.exception.ValidationGroups;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeTokenRequest {
	@NotNull(message = "알림 동의 여부", groups = ValidationGroups.NotEmptyGroup.class)
	private boolean noticeAgree;
	private String deviceToken;
}
