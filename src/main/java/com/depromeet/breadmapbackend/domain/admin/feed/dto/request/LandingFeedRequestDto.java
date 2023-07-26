package com.depromeet.breadmapbackend.domain.admin.feed.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LandingFeedRequestDto {

	private String redirectUrl;

	public LandingFeedRequestDto(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
}
