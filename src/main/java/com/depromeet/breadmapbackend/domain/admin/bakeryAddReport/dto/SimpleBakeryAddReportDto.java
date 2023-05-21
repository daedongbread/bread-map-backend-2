package com.depromeet.breadmapbackend.domain.admin.bakeryAddReport.dto;


import com.depromeet.breadmapbackend.domain.bakery.report.BakeryAddReport;
import com.depromeet.breadmapbackend.domain.bakery.report.BakeryAddReportStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class SimpleBakeryAddReportDto {
    private Long reportId;
    private Long userId;
    private String nickName;
    private String bakeryName;
    private String location;
    private String content;
    private String createdAt;
    private BakeryAddReportStatus status;

    @Builder
    public SimpleBakeryAddReportDto(BakeryAddReport bakeryAddReport) {
        this.reportId = bakeryAddReport.getId();
        this.userId = bakeryAddReport.getUser().getId();
        this.nickName = bakeryAddReport.getUser().getUserInfo().getNickName();
        this.bakeryName = bakeryAddReport.getName();
        this.location = bakeryAddReport.getLocation();
        this.content = bakeryAddReport.getContent();
        this.createdAt = bakeryAddReport.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.status = bakeryAddReport.getStatus();
    }
}