package com.depromeet.breadmapbackend.domain.admin.feed.dto.response;

import java.time.LocalDateTime;

import com.depromeet.breadmapbackend.domain.admin.feed.domain.Feed;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedContentInfo {
	private Long feedId;
	private String feedTitle;
	private String authorName;
	private LocalDateTime createdAt;
	private String categoryName;
	private String isActive;

	@Builder
	public FeedContentInfo(Long feedId, String feedTitle, String authorName, LocalDateTime createdAt,
		String categoryName, String isActive) {
		this.feedId = feedId;
		this.feedTitle = feedTitle;
		this.authorName = authorName;
		this.createdAt = createdAt;
		this.categoryName = categoryName;
		this.isActive = isActive;
	}

	public static FeedContentInfo of(Feed feed) {
		return FeedContentInfo.builder()
			.feedId(feed.getId())
			.feedTitle(feed.getSubTitle())
			.authorName(feed.getAdmin().getEmail())
			.createdAt(feed.getCreatedAt())
			.categoryName(feed.getCategory().getCategoryName())
			.isActive(feed.getActivated() == FeedStatus.POSTING ? "게시중" : "미게시")
			.build();
	}
}
