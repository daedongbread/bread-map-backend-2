package com.depromeet.breadmapbackend.domain.admin.bakery.dto;

import com.depromeet.breadmapbackend.domain.bakery.report.BakeryUpdateReason;
import com.depromeet.breadmapbackend.domain.bakery.report.BakeryUpdateReportImage;
import com.depromeet.breadmapbackend.domain.bakery.report.BakeryUpdateReport;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class BakeryUpdateReportDto {
    private Long reportId;
    private LocalDateTime createdAt;
    private String nickName;
    private BakeryUpdateReason reason;
    private String content;
    private List<String> imageList;
    private Boolean isChange;

    @Builder
    public BakeryUpdateReportDto(BakeryUpdateReport report) {
        this.reportId = report.getId();
        this.createdAt = report.getCreatedAt();
        this.nickName = report.getUser().getNickName();
        this.reason = report.getReason();
        this.content = report.getContent();
        this.imageList = report.getImages().stream().map(BakeryUpdateReportImage::getImage).collect(Collectors.toList());
        this.isChange = report.getIsChange();
    }
}
