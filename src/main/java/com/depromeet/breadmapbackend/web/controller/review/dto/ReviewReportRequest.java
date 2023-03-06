package com.depromeet.breadmapbackend.web.controller.review.dto;

import com.depromeet.breadmapbackend.domain.review.ReviewReportReason;
import com.depromeet.breadmapbackend.web.advice.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReportRequest {
    private ReviewReportReason reason;
    @Size(min=10, max=500, message = "10자 이상, 500자 이하 입력해주세요.", groups = ValidationGroups.SizeCheckGroup.class)
    private String content;
}
