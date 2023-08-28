package com.depromeet.breadmapbackend.domain.admin.feed.dto.request;

import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedSearchRequest {

	private String createdAt;
	private String createBy;
	private FeedStatus activated;
	private String categoryName;

	public FeedSearchRequest(String createdAt, String createBy, FeedStatus activated, String categoryName) {
		this.createdAt = createdAt;
		this.createBy = createBy;
		this.activated = activated;
		this.categoryName = categoryName;
	}
}
