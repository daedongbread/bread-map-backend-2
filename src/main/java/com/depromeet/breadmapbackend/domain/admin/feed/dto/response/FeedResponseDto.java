package com.depromeet.breadmapbackend.domain.admin.feed.dto.response;

import java.util.List;

import com.depromeet.breadmapbackend.domain.admin.feed.domain.CurationBakery;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.CurationFeed;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.Feed;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedResponseDto {

	private CommonFeedResponseDto common;

	private List<CurationFeedResponseDto> curation;

	private LandingFeedResponseDto landing;

	@Builder
	private FeedResponseDto(CommonFeedResponseDto common, List<CurationFeedResponseDto> curation,
		LandingFeedResponseDto landing) {
		this.common = common;
		this.curation = curation;
		this.landing = landing;
	}

	public static FeedResponseDto ofLanding(Feed feed) {
		return FeedResponseDto.builder()
			.common(CommonFeedResponseDto.of(feed))
			.landing(LandingFeedResponseDto.of(feed))
			.build();
	}

	public static FeedResponseDto ofCuration(Feed feed) {

		CurationFeed curationFeed = (CurationFeed)feed;
		List<CurationBakery> bakeries = curationFeed.getBakeries().getBakeries();

		List<CurationFeedResponseDto> curationFeedResponseDtos = bakeries.stream()
			.map(CurationFeedResponseDto::of)
			.toList();

		return FeedResponseDto.builder()
			.common(CommonFeedResponseDto.of(feed))
			.curation(curationFeedResponseDtos)
			.build();
	}
}
