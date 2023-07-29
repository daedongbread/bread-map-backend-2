package com.depromeet.breadmapbackend.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedLikeResponse {

	private Long userId;
	private String likeStatus;
	private int likeCounts;

	@Builder
	public FeedLikeResponse(Long userId, String likeStatus, int likeCounts) {
		this.userId = userId;
		this.likeStatus = likeStatus;
		this.likeCounts = likeCounts;
	}
}
