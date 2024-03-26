package com.depromeet.breadmapbackend.domain.review.tag.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TagResponse {
    private final long order;
    private final String tagName;
    private final String tagDescription;
}
