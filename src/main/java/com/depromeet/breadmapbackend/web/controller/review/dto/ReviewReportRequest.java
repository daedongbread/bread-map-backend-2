package com.depromeet.breadmapbackend.web.controller.review.dto;

import com.depromeet.breadmapbackend.domain.review.ReviewReportReason;
import com.depromeet.breadmapbackend.web.advice.ValidationGroups;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class ReviewReportRequest {
    private ReviewReportReason reason;
    @NotBlank(message = "내용은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private String content;

    @Builder
    public ReviewReportRequest(ReviewReportReason reason, String content) {
        this.reason = reason;
        this.content = content;
    }
}
