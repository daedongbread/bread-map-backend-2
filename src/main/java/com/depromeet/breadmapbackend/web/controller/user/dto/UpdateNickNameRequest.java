package com.depromeet.breadmapbackend.web.controller.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNickNameRequest {
    @Size(min = 10, message = "10자 이상 입력해주세요.")
    public String nickName;
}
