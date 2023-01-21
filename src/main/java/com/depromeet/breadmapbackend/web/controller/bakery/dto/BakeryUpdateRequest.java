package com.depromeet.breadmapbackend.web.controller.bakery.dto;

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
public class BakeryUpdateRequest {
    @NotBlank(message = "이름은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    @Size(min=1, max=20, message = "1자 이상, 20자 이하 입력해주세요.", groups = ValidationGroups.SizeCheckGroup.class)
    private String name;
    @NotBlank(message = "위치는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    @Size(min=3, max=100, message = "3자 이상, 100자 이하 입력해주세요.", groups = ValidationGroups.SizeCheckGroup.class)
    private String location;
    @NotBlank(message = "수정 사항은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    @Size(min=10, max=500, message = "10자 이상, 500자 이하 입력해주세요.", groups = ValidationGroups.SizeCheckGroup.class)
    private String content;
}
