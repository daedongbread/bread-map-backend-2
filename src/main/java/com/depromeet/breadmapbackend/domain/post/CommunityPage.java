package com.depromeet.breadmapbackend.domain.post;

public record CommunityPage(
	long reviewOffset,
	long postOffset,
	PostTopic topic,
	int page
) {
}