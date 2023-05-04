package com.depromeet.breadmapbackend.domain.flag.dto;

import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.depromeet.breadmapbackend.domain.flag.FlagIcon;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FlagInfo {
    private Long id;
    private String name;
    private FlagColor color;
    private FlagIcon icon;
    private Integer bakeryNum;

    @Builder
    public FlagInfo(Flag flag) {
        this.id = flag.getId();
        this.name = flag.getName();
        this.color = flag.getColor();
        this.icon = (flag.getName().equals("가봤어요")) ? FlagIcon.FLAG : FlagIcon.HEART;
        this.bakeryNum = flag.getFlagBakeryList().size();
    }
}
