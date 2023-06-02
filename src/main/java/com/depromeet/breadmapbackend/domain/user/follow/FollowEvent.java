package com.depromeet.breadmapbackend.domain.user.follow;

import com.depromeet.breadmapbackend.global.event.NoticableEvent;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FollowEvent extends NoticableEvent {

	private final Long fromUserId;

	@Builder
	public FollowEvent(final Long userId, final Long fromUserId) {
		super(userId);
		this.fromUserId = fromUserId;
	}
}
