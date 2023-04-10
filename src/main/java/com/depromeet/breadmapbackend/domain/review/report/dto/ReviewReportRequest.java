package com.depromeet.breadmapbackend.domain.review.report.dto;

import com.depromeet.breadmapbackend.domain.review.report.ReviewReportReason;
import com.depromeet.breadmapbackend.global.exception.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReportRequest {
    private ReviewReportReason reason;
    private String content;
}
