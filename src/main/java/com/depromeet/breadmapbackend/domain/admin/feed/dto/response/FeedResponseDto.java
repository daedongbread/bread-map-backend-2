package com.depromeet.breadmapbackend.domain.admin.feed.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedResponseDto {

	private CommonFeedResponseDto common;

	private List<CurationFeedResponseDto> curation;

	private LandingFeedResponseDto landing;
	private int likeCounts;

	@Builder
	private FeedResponseDto(CommonFeedResponseDto common, List<CurationFeedResponseDto> curation,
		LandingFeedResponseDto landing, int likeCounts) {
		this.common = common;
		this.curation = curation;
		this.landing = landing;
		this.likeCounts = likeCounts;
	}
}
