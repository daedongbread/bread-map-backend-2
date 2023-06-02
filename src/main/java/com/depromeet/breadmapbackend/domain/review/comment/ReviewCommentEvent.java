package com.depromeet.breadmapbackend.domain.review.comment;

import com.depromeet.breadmapbackend.global.event.NoticableEvent;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewCommentEvent extends NoticableEvent {
	private final Long fromUserId;
	private final Long reviewId;
	private final String reviewContent;

	@Builder
	public ReviewCommentEvent(
		final Long userId,
		final Long fromUserId,
		final Long reviewId,
		final String reviewContent
	) {
		super(userId);
		this.fromUserId = fromUserId;
		this.reviewId = reviewId;
		this.reviewContent = reviewContent;
	}

}
