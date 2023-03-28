package com.depromeet.breadmapbackend.domain.flag.dto;

import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FlagInfo {
    private Long id;
    private String name;
    private FlagColor color;

    @Builder
    public FlagInfo(Flag flag) {
        this.id = flag.getId();
        this.name = flag.getName();
        this.color = flag.getColor();
    }
}
