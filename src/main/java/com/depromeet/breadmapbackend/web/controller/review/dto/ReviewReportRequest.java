package com.depromeet.breadmapbackend.web.controller.review.dto;

import com.depromeet.breadmapbackend.domain.review.ReviewReportReason;
import com.depromeet.breadmapbackend.web.advice.ValidationGroups;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class ReviewReportRequest {
    private ReviewReportReason reason;
    @NotBlank(message = "내용은 필수 값입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    @Size(min=10, max=500, message = "10자 이상, 500자 이하 입력해주세요.", groups = ValidationGroups.SizeCheckGroup.class)
    private String content;

    @Builder
    public ReviewReportRequest(ReviewReportReason reason, String content) {
        this.reason = reason;
        this.content = content;
    }
}
