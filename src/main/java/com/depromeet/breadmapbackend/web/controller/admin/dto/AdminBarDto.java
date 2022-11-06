package com.depromeet.breadmapbackend.web.controller.admin.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminBarDto {
    private Integer bakeryReportCount;
    private Integer bakeryCount;
    private Integer reviewReportCount;

    @Builder
    public AdminBarDto(Integer bakeryReportCount, Integer bakeryCount, Integer reviewReportCount) {
        this.bakeryReportCount = bakeryReportCount;
        this.bakeryCount = bakeryCount;
        this.reviewReportCount = reviewReportCount;
    }
}
