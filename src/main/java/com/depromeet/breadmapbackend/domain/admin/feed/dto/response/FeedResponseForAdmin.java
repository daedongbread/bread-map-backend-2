package com.depromeet.breadmapbackend.domain.admin.feed.dto.response;

import java.time.LocalDateTime;

import com.depromeet.breadmapbackend.domain.admin.feed.domain.Feed;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedResponseForAdmin {

	private Long feedId;
	private String feedTitle;
	private String authorName;
	private LocalDateTime activeTime;
	private String isActive;

	@Builder
	private FeedResponseForAdmin(Long feedId, String feedTitle, String authorName, LocalDateTime activeTime,
		String isActive) {
		this.feedId = feedId;
		this.feedTitle = feedTitle;
		this.authorName = authorName;
		this.activeTime = activeTime;
		this.isActive = isActive;
	}

	public static FeedResponseForAdmin of(Feed feed) {
		return FeedResponseForAdmin.builder()
			.feedId(feed.getId())
			.feedTitle(feed.getSubTitle())
			.authorName(feed.getAdmin().getEmail())
			.activeTime(feed.getActiveTime())
			.isActive(feed.getActivated() == FeedStatus.POSTING ? "게시중" : "미게시")
			.build();
	}

	@Override
	public String toString() {
		return "FeedResponseForAdmin{" +
			"feedId=" + feedId +
			", feedTitle='" + feedTitle + '\'' +
			", authorName='" + authorName + '\'' +
			", activeTime=" + activeTime +
			", isActive='" + isActive + '\'' +
			'}';
	}
}
