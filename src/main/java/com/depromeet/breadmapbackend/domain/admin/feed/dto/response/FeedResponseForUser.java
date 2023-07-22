package com.depromeet.breadmapbackend.domain.admin.feed.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedResponseForUser {

	private Long feedId;
	private String imageUrl;
	private String feedType;
	private String redirectUrl;

	@Builder
	public FeedResponseForUser(Long feedId, String imageUrl, String feedType, String redirectUrl) {
		this.feedId = feedId;
		this.imageUrl = imageUrl;
		this.feedType = feedType;
		this.redirectUrl = redirectUrl;
	}
}
