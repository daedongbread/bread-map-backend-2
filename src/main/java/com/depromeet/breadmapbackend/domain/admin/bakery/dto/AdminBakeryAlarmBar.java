package com.depromeet.breadmapbackend.domain.admin.bakery.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminBakeryAlarmBar {
    private Integer newAlarmNum;

    @Builder
    public AdminBakeryAlarmBar(
            Integer bakeryReportImageNum, Integer productAddReportNum, Integer bakeryUpdateReportNum, Integer newReviewNum) {
        this.newAlarmNum = bakeryReportImageNum + productAddReportNum + bakeryUpdateReportNum + newReviewNum;
    }
}
