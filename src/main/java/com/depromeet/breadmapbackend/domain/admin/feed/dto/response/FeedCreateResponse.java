package com.depromeet.breadmapbackend.domain.admin.feed.dto.response;

import lombok.Getter;

@Getter
public class FeedCreateResponse {

	private Long feedId;

	public FeedCreateResponse(Long feedId) {
		this.feedId = feedId;
	}
}
