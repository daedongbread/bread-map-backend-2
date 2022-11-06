package com.depromeet.breadmapbackend.web.controller.admin.dto;

import com.depromeet.breadmapbackend.domain.review.ReviewReport;
import com.depromeet.breadmapbackend.domain.review.ReviewReportReason;
import com.depromeet.breadmapbackend.domain.review.ReviewStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class AdminReviewReportDto {
    private Long reviewReportId;
    private String reporterNickName;
    private ReviewReportReason reason;
    private String respondentNickName;
    private Long reportedReviewId;
    private String content;
    private String createdAt;
    private boolean isBlock;

    @Builder
    public AdminReviewReportDto(ReviewReport reviewReport) {
        this.reviewReportId = reviewReport.getId();
        this.reporterNickName = reviewReport.getReporter().getNickName();
        this.reason = reviewReport.getReason();
        this.respondentNickName = reviewReport.getReview().getUser().getNickName();
        this.reportedReviewId = reviewReport.getReview().getId();
        this.content = reviewReport.getContent();
        this.createdAt = reviewReport.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.isBlock = reviewReport.getIsBlock();
    }
}
