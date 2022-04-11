package com.depromeet.breadmapbackend.web.controller.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleReviewDto {
    private Long id;
    private String content;
}
