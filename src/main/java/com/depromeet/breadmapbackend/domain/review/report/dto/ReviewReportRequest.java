package com.depromeet.breadmapbackend.domain.review.report.dto;

import com.depromeet.breadmapbackend.domain.review.report.ReviewReportReason;
import com.depromeet.breadmapbackend.global.annotation.EnumCheck;
import com.depromeet.breadmapbackend.global.exception.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReportRequest {
    @EnumCheck(groups = ValidationGroups.PatternCheckGroup.class)
    private ReviewReportReason reason;
    private String content;
}
