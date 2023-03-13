package com.depromeet.breadmapbackend.web.controller.admin.dto;

import com.depromeet.breadmapbackend.web.advice.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAddImageRegisterRequest {
    @NotEmpty(message = "이미지 리스트는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private List<@NotNull(message = "이미지 고유 번호는 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class) Long> imageIdList;
}
