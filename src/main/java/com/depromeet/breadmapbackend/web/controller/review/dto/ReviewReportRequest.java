package com.depromeet.breadmapbackend.web.controller.review.dto;

import com.depromeet.breadmapbackend.domain.review.ReviewReportReason;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewReportRequest {
    private ReviewReportReason reason;
    private String content;

    @Builder
    public ReviewReportRequest(ReviewReportReason reason, String content) {
        this.reason = reason;
        this.content = content;
    }
}
