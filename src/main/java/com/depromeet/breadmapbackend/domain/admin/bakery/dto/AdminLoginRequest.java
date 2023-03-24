package com.depromeet.breadmapbackend.domain.admin.bakery.dto;

import com.depromeet.breadmapbackend.global.exception.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginRequest {
    @NotBlank(message = "관리자 이메일은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private String email;
    @NotBlank(message = "비밀번호는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private String password;
}
