package com.depromeet.breadmapbackend.domain.bakery.report;

import com.depromeet.breadmapbackend.global.event.NoticableEvent;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BakeryAddEvent extends NoticableEvent {
	private final Long bakeryId;
	private final String bakeryName;

	@Builder
	public BakeryAddEvent(
		final Long userId,
		final Long bakeryId,
		final String bakeryName
	) {
		super(userId);
		this.bakeryId = bakeryId;
		this.bakeryName = bakeryName;
	}
}
