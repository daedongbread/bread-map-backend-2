package com.depromeet.breadmapbackend.domain.review.comment.like;

import com.depromeet.breadmapbackend.global.event.NoticableEvent;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewCommentLikeEvent extends NoticableEvent {
	private final Long fromUserId;
	private final Long commentId;
	private final String commentContent;

	@Builder
	public ReviewCommentLikeEvent(
		final Long userId,
		final Long fromUserId,
		final Long commentId,
		final String commentContent
	) {
		super(userId);
		this.fromUserId = fromUserId;
		this.commentId = commentId;
		this.commentContent = commentContent;
	}
}
