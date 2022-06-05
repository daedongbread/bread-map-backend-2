package com.depromeet.breadmapbackend.web.controller.flag.dto;

import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.depromeet.breadmapbackend.web.advice.ValidationGroups;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AddFlagRequest {
    @NotBlank(message = "이름은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private String name;
    private FlagColor color;

    @Builder
    public AddFlagRequest(String name, FlagColor color) {
        this.name = name;
        this.color = color;
    }
}
