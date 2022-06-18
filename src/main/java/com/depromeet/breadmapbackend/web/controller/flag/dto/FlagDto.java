package com.depromeet.breadmapbackend.web.controller.flag.dto;

import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class FlagDto {
    private Long flagId;
    private String name;
    private FlagColor color;
    private List<String> bakeryImageList;

    @Builder
    public FlagDto(Long flagId, String name, FlagColor color, List<String> bakeryImageList) {
        this.flagId = flagId;
        this.name = name;
        this.color = color;
        this.bakeryImageList = bakeryImageList;
    }
}
