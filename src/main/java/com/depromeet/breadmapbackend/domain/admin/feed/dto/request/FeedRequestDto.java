package com.depromeet.breadmapbackend.domain.admin.feed.dto.request;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedRequestDto {

	private CommonFeedRequestDto common;

	private List<CurationFeedRequestDto> curation;

	private LandingFeedRequestDto landing;

	public FeedRequestDto(
		CommonFeedRequestDto common,
		LandingFeedRequestDto landing
	) {
		this.common = common;
		this.curation = null;
		this.landing = landing;
	}

	public FeedRequestDto(
		CommonFeedRequestDto common,
		List<CurationFeedRequestDto> curation
	) {
		this.common = common;
		this.curation = curation;
		this.landing = null;
	}

	@JsonIgnore
	public List<Long> getBakeryIds() {
		return this.curation.stream()
			.map(CurationFeedRequestDto::getBakeryId)
			.collect(Collectors.toList());
	}

	@JsonIgnore
	public CurationFeedRequestDto findCuration(Long bakeryId) {
		return this.curation.stream()
			.filter(c -> Objects.equals(c.getBakeryId(), bakeryId))
			.findAny()
			.orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
	}
}
