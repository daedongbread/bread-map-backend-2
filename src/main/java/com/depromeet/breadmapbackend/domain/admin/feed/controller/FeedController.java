package com.depromeet.breadmapbackend.domain.admin.feed.controller;

import java.util.List;

import com.depromeet.breadmapbackend.domain.admin.dto.FeedLikeResponse;
import com.depromeet.breadmapbackend.global.security.userinfo.CurrentUserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedType;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.FeedResponseDto;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.FeedResponseForUser;
import com.depromeet.breadmapbackend.domain.admin.feed.service.CommonFeedService;
import com.depromeet.breadmapbackend.domain.admin.feed.service.FeedService;
import com.depromeet.breadmapbackend.domain.admin.feed.service.FeedServiceFactory;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/feed")
public class FeedController {

	private final FeedServiceFactory serviceFactory;
	private final CommonFeedService commonFeedService;

	@GetMapping("/all")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<List<FeedResponseForUser>> readAllFeed() {

		List<FeedResponseForUser> feeds = commonFeedService.getAllFeedForUser();

		return new ApiResponse<>(feeds);
	}

	@GetMapping("/{feedId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<FeedResponseDto> readFeed(
		@PathVariable Long feedId,
		@RequestParam FeedType feedType
	) {
		FeedService feedService = serviceFactory.getService(feedType);

		FeedResponseDto feed = feedService.getFeed(feedId);

		return new ApiResponse<>(feed);
	}

	@PatchMapping("/{feedId}/like")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<FeedLikeResponse> like(
		@PathVariable Long feedId,
		@AuthenticationPrincipal CurrentUserInfo currentUserInfo
	) {
		FeedLikeResponse response = commonFeedService.likeFeed(currentUserInfo.getId(), feedId);

		return new ApiResponse<>(response);
	}

	@PatchMapping("/{feedId}/unlike")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<FeedLikeResponse> unLike(
			@PathVariable Long feedId,
			@AuthenticationPrincipal CurrentUserInfo currentUserInfo
	) {
		FeedLikeResponse response = commonFeedService.unLikeFeed(currentUserInfo.getId(), feedId);

		return new ApiResponse<>(response);
	}
}
