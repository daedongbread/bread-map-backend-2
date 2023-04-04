package com.depromeet.breadmapbackend.domain.admin.bakery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminImageBarDto {
    private Integer bakeryReportImageNum;
    private Integer productAddReportImageNum;
    private Integer reviewImageNum;
}
