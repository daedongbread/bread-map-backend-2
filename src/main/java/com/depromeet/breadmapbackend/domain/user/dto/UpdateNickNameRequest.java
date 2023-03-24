package com.depromeet.breadmapbackend.domain.user.dto;

import com.depromeet.breadmapbackend.global.exception.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNickNameRequest {
    @NotBlank(message = "닉네임은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    @Size(min=2, max=10, message = "2자 이상, 10자 이하 입력해주세요.", groups = ValidationGroups.SizeCheckGroup.class)
    @Pattern(regexp = "^[0-9a-zA-Z가-힣]*$",message = "닉네임은 숫자, 영어, 한글만 가능합니다.", groups = ValidationGroups.PatternCheckGroup.class)
    public String nickName;
}
