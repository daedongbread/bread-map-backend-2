package com.depromeet.breadmapbackend.domain.flag.dto;

import com.depromeet.breadmapbackend.domain.flag.Flag;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class FlagDto {
    private FlagInfo flagInfo;
    private List<String> bakeryImageList;

    @Builder
    public FlagDto(Flag flag, List<String> bakeryImageList) {
        this.flagInfo = FlagInfo.builder().flag(flag).build();
        this.bakeryImageList = bakeryImageList;
    }
}
