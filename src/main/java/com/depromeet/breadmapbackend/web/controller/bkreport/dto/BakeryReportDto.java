package com.depromeet.breadmapbackend.web.controller.bkreport.dto;

import com.depromeet.breadmapbackend.domain.bkreport.BkReport;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class BakeryReportDto {
    private Long id;
    private Long userId;
    private String bakeryName;
    private LocalDateTime createdAt;
    private String address;
    private String reason;
    private Integer status;

    @Builder
    public BakeryReportDto(BkReport bkReport) {
        this.id = bkReport.getId();
        this.userId = bkReport.getUser().getId();
        this.bakeryName = bkReport.getBakeryName();
        this.createdAt = bkReport.getCreatedAt();
        this.address = bkReport.getAddress();
        this.reason = bkReport.getReason();
        this.status = bkReport.getStatus();
    }
}
