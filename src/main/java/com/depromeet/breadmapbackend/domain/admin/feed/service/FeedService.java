package com.depromeet.breadmapbackend.domain.admin.feed.service;

import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedType;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.FeedRequestDto;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.FeedResponseDto;

public interface FeedService {
	FeedType getServiceType();

	void updateFeed(Long feedId, FeedRequestDto updateDto);

	Long addFeed(Long adminId, FeedRequestDto requestDto);

	FeedResponseDto getFeed(Long feedId);
}
