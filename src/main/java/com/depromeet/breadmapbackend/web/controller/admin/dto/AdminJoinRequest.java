package com.depromeet.breadmapbackend.web.controller.admin.dto;

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
public class AdminJoinRequest {
    @NotBlank(message = "관리자 아이디는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private String adminId;
    @NotBlank(message = "비밀번호는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private String password;
    @NotBlank(message = "관리자 비밀 값은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private String secret;
}
