package com.depromeet.breadmapbackend.web.controller.user.dto;

import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UserFlagDto {
    private Long flagId;
    private String name;
    private FlagColor color;
    private List<String> flagImageList;

    @Builder
    public UserFlagDto(Long flagId, String name, FlagColor color, List<String> flagImageList) {
        this.flagId = flagId;
        this.name = name;
        this.color = color;
        this.flagImageList = flagImageList;
    }
}
