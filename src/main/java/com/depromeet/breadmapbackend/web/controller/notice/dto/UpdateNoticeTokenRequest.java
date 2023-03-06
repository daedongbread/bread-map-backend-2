package com.depromeet.breadmapbackend.web.controller.notice.dto;

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
public class UpdateNoticeTokenRequest {
    @NotBlank(message = "이전 기기 값은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private String oldDeviceToken;
    @NotBlank(message = "새 기기 값은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private String newDeviceToken;
}
