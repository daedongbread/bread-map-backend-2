package com.depromeet.breadmapbackend.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminBarDto {
    private Integer bakeryAddReportCount;
    private Integer bakeryCount;
    private Integer reviewReportCount;

    @Builder
    public AdminBarDto(Integer bakeryAddReportCount, Integer bakeryCount, Integer reviewReportCount) {
        this.bakeryAddReportCount = bakeryAddReportCount;
        this.bakeryCount = bakeryCount;
        this.reviewReportCount = reviewReportCount;
    }
}
