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
public class ProductReportRequest {
    @NotBlank(message = "이름은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    @Size(min=1, max=20, message = "1자 이상, 20자 이하 입력해주세요.", groups = ValidationGroups.SizeCheckGroup.class)
    private String name;
    @NotBlank(message = "가격은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    @Size(min=3, max=10, message = "3자 이상, 10자 이하 입력해주세요.", groups = ValidationGroups.SizeCheckGroup.class)
    private String price;
}
