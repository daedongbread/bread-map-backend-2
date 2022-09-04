package com.depromeet.breadmapbackend.web.controller.bakery.dto;

import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.web.controller.review.dto.ReviewDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BakeryDto {
    private BakeryInfo info;
    private List<BreadDto> menu;
//    private List<ReviewDto> review;
    private List<FacilityInfo> facilityInfoList;

    @Builder
    public BakeryDto(BakeryInfo info, List<BreadDto> menu, /*List<ReviewDto> review, */List<FacilityInfo> facilityInfoList) {
        this.info = info;
        this.menu = menu;
//        this.review = review;
        this.facilityInfoList = facilityInfoList;
    }
}
