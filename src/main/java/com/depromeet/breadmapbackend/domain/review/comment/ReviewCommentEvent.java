package com.depromeet.breadmapbackend.domain.review.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReviewCommentEvent {
    private Long userId;
    private Long fromUserId;
    private Long reviewId;
    private String reviewContent;
}
