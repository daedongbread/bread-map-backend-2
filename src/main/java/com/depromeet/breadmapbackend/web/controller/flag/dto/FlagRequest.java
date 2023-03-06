package com.depromeet.breadmapbackend.web.controller.flag.dto;

import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.depromeet.breadmapbackend.web.advice.ValidationGroups;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class FlagRequest {
    @NotBlank(message = "이름은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    @Size(min=1, max=20, message = "1자 이상, 20자 이하 입력해주세요.", groups = ValidationGroups.SizeCheckGroup.class)
    private String name;
    private FlagColor color;
}
