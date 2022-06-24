package com.depromeet.breadmapbackend.web.controller.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCommentRequest {
    private String content;
    private Long parentCommentId;
}
