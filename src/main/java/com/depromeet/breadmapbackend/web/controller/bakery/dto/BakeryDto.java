package com.depromeet.breadmapbackend.web.controller.bakery.dto;

import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BakeryDto {
    private BakeryInfo info;
    private List<BreadDto> menu;
    private List<FacilityInfo> facilityInfoList;

    @Builder
    public BakeryDto(BakeryInfo info, List<BreadDto> menu, List<FacilityInfo> facilityInfoList) {
        this.info = info;
        this.menu = menu;
        this.facilityInfoList = facilityInfoList;
    }
}
