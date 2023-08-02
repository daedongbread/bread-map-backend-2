package com.depromeet.breadmapbackend.domain.admin.feed.dto.response;

import java.util.List;

import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
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
	private String likeStatus;

	@Builder
	private FeedResponseDto(CommonFeedResponseDto common, List<CurationFeedResponseDto> curation,
		LandingFeedResponseDto landing, int likeCounts, String likeStatus) {
		this.common = common;
		this.curation = curation;
		this.landing = landing;
		this.likeCounts = likeCounts;
		this.likeStatus = likeStatus;
	}

	public void setIsFlagged(List<FlagBakery> flagBakeryList) {
		for (CurationFeedResponseDto cur : curation) {
			flagBakeryList.forEach(cur::setIsFlagged);
		}
	}
}
