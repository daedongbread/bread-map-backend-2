package com.depromeet.breadmapbackend.web.controller.admin.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BakeryAddReportListDto {
    private List<SimpleBakeryAddReportDto> bakeryAddReportList;
    private int totalNum;

    @Builder
    public BakeryAddReportListDto(List<SimpleBakeryAddReportDto> bakeryAddReportDtoList, int totalNum) {
        this.bakeryAddReportList = bakeryAddReportDtoList;
        this.totalNum = totalNum;
    }
}
