package com.depromeet.breadmapbackend.domain.post;

public record CommunityPage(
	long reviewOffset,
	long postOffset,
	int page
) {
}