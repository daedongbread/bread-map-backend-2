package com.depromeet.breadmapbackend.domain.admin.feed.dto.response;

import com.depromeet.breadmapbackend.domain.admin.feed.domain.Feed;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommonFeedResponseDto {

	private Long feedId;
	private String subTitle;
	private String introduction;
	private String conclusion;
	private String thumbnailUrl;
	private String feedType;
	private String categoryName;
	private String activated;
	private String activateTime;

	@Builder
	public CommonFeedResponseDto(long feedId, String subTitle, String introduction, String conclusion,
		String thumbnailUrl,
		String feedType,
		String categoryName,
		String activated,
		String activateTime) {
		this.feedId = feedId;
		this.subTitle = subTitle;
		this.introduction = introduction;
		this.conclusion = conclusion;
		this.thumbnailUrl = thumbnailUrl;
		this.feedType = feedType;
		this.categoryName = categoryName;
		this.activated = activated;
		this.activateTime = activateTime;
	}

	public static CommonFeedResponseDto of(Feed feed) {
		return CommonFeedResponseDto.builder()
			.feedId(feed.getId())
			.subTitle(feed.getSubTitle())
			.introduction(feed.getIntroduction())
			.conclusion(feed.getConclusion())
			.thumbnailUrl(feed.getThumbnailUrl())
			.feedType(feed.getFeedType())
			.activated(feed.getActivated().toString())
			.categoryName(feed.getCategory().getCategoryName())
			.activateTime(feed.getActiveTime().toString())
			.build();
	}
}
