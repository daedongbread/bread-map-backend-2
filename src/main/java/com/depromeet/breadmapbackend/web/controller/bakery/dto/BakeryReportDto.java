package com.depromeet.breadmapbackend.web.controller.bakery.dto;


import com.depromeet.breadmapbackend.domain.bakery.BakeryAddReport;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class BakeryReportDto {
    private Long id;
    private Long userId;
    private String name;
    private LocalDateTime createdAt;
    private String location;
    private String content;
    private Integer status;

    @Builder
    public BakeryReportDto(BakeryAddReport bakeryAddReport) {
        this.id = bakeryAddReport.getId();
        this.userId = bakeryAddReport.getUser().getId();
        this.name = bakeryAddReport.getName();
        this.createdAt = bakeryAddReport.getCreatedAt();
        this.location = bakeryAddReport.getLocation();
        this.content = bakeryAddReport.getContent();
        this.status = bakeryAddReport.getStatus();
    }
}