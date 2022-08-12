package com.depromeet.breadmapbackend.web.controller.admin.dto;

import com.depromeet.breadmapbackend.domain.bakery.BakeryAddReport;
import com.depromeet.breadmapbackend.domain.bakery.BakeryAddReportStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class BakeryAddReportDto {
    private String nickName;
    private String bakeryName;
    private String location;
    private String content;
    private BakeryAddReportStatus status;

    @Builder
    public BakeryAddReportDto(BakeryAddReport bakeryAddReport) {
        this.nickName = bakeryAddReport.getUser().getNickName();
        this.bakeryName = bakeryAddReport.getName();
        this.location = bakeryAddReport.getLocation();
        this.content = bakeryAddReport.getContent();
        this.status = bakeryAddReport.getStatus();
    }
}
