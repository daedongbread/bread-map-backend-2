package com.depromeet.breadmapbackend.domain.review.comment.like;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReviewCommentLikeEvent {
    private Long userId;
    private Long fromUserId;
    private Long commentId;
    private String commentContent;
}
