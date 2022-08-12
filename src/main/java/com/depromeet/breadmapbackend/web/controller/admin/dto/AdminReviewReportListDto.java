package com.depromeet.breadmapbackend.web.controller.admin.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AdminReviewReportListDto {
    private List<AdminReviewReportDto> reviewReportDtoList;
    private int totalNum;

    @Builder
    public AdminReviewReportListDto(List<AdminReviewReportDto> reviewReportDtoList, int totalNum) {
        this.reviewReportDtoList = reviewReportDtoList;
        this.totalNum = totalNum;
    }
}
