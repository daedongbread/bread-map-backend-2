package com.depromeet.breadmapbackend.global.event;

public abstract class NoticableEvent {

	public final Long userId;

	public NoticableEvent(Long userId) {
		this.userId = userId;
	}

	public final Long getUserId() {
		return userId;
	}
}
