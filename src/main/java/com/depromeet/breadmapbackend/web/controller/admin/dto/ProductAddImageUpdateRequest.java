package com.depromeet.breadmapbackend.web.controller.admin.dto;

import com.depromeet.breadmapbackend.web.advice.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAddImageUpdateRequest {
    @NotBlank(message = "이전 메인 이미지는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private String beforeImage;

    @NotNull(message = "이후 메인 이미지 고유 번호는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private Long afterId;
}
