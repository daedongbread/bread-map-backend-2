package com.depromeet.breadmapbackend.domain.admin.bakeryAddReport.dto;

import com.depromeet.breadmapbackend.domain.bakery.report.BakeryAddReport;
import com.depromeet.breadmapbackend.domain.bakery.report.BakeryAddReportStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
        this.nickName = bakeryAddReport.getUser().getUserInfo().getNickName();
        this.bakeryName = bakeryAddReport.getName();
        this.location = bakeryAddReport.getLocation();
        this.content = bakeryAddReport.getContent();
        this.status = bakeryAddReport.getStatus();
    }
}
