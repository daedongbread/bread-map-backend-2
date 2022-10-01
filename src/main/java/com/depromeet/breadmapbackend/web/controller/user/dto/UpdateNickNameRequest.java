package com.depromeet.breadmapbackend.web.controller.user.dto;

import com.depromeet.breadmapbackend.web.advice.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNickNameRequest {
    @NotBlank(message = "닉네임은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    @Size(min = 10, message = "10자 이상 입력해주세요.", groups = ValidationGroups.PatternCheckGroup.class)
    public String nickName;
}
