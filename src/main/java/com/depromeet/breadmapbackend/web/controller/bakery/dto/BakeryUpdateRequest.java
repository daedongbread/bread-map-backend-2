package com.depromeet.breadmapbackend.web.controller.bakery.dto;

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
public class BakeryUpdateRequest {
    @NotBlank(message = "이름은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private String name;
    @NotBlank(message = "위치는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private String location;
    @NotBlank(message = "수정 사항은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private String content;
}
