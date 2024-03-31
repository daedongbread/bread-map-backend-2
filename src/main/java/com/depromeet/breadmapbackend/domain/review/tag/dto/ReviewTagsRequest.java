package com.depromeet.breadmapbackend.domain.review.tag.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewTagsRequest {
    private long challengeId;
    private List<TagResponse> bakeryTags;
    private List<TagResponse> breadTags;
}
