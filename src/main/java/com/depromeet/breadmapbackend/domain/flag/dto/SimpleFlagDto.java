package com.depromeet.breadmapbackend.domain.flag.dto;

import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SimpleFlagDto {
    private Long flagId;
    private String name;
    private FlagColor color;

    @Builder
    public SimpleFlagDto(Long flagId, String name, FlagColor color) {
        this.flagId = flagId;
        this.name = name;
        this.color = color;
    }
}
