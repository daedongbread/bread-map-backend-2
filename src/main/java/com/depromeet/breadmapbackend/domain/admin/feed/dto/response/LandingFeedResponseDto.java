package com.depromeet.breadmapbackend.domain.admin.feed.dto.response;

import com.depromeet.breadmapbackend.domain.admin.feed.domain.Feed;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.LandingFeed;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LandingFeedResponseDto {

	private String redirectUrl;

	public LandingFeedResponseDto(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public static LandingFeedResponseDto of(Feed feed) {

		LandingFeed landingFeed = (LandingFeed)feed;

		return new LandingFeedResponseDto(landingFeed.getRedirectUrl());
	}
}
