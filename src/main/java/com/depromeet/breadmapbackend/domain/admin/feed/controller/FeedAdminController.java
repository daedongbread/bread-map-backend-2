package com.depromeet.breadmapbackend.domain.admin.feed.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedStatus;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedType;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.FeedRequestDto;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.FeedSearchRequest;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.FeedResponseDto;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.FeedResponseForAdmin;
import com.depromeet.breadmapbackend.domain.admin.feed.service.CommonFeedService;
import com.depromeet.breadmapbackend.domain.admin.feed.service.FeedService;
import com.depromeet.breadmapbackend.domain.admin.feed.service.FeedServiceFactory;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.security.userinfo.CurrentUserInfo;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/feed")
public class FeedAdminController {

	private final FeedServiceFactory serviceFactory;
	private final CommonFeedService commonFeedService;
	private static final String REDIRECT_URL = "/v1/admin/feed/%d";

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void addFeed(
		@AuthenticationPrincipal CurrentUserInfo currentUserInfo,
		@RequestBody FeedRequestDto requestDto,
		HttpServletResponse response
	) {
		FeedService feedService = serviceFactory.getService(requestDto.getCommon().getFeedType());

		Long feedId = feedService.addFeed(currentUserInfo.getId(), requestDto);

		String redirectUrl = String.format(REDIRECT_URL, feedId);

		response.setHeader("Location", redirectUrl);
	}

	@PatchMapping("/{feedId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateFeed(
		@AuthenticationPrincipal CurrentUserInfo currentUserInfo,
		@PathVariable Long feedId,
		@Valid @RequestBody FeedRequestDto updateDto
	) {
		FeedService feedService = serviceFactory.getService(updateDto.getCommon().getFeedType());

		feedService.updateFeed(currentUserInfo.getId(), feedId, updateDto);
	}

	@GetMapping("/all")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<FeedResponseForAdmin> readAllFeed(
		@RequestParam(required = false) String createdAt,
		@RequestParam(required = false) String createBy,
		@RequestParam(required = false) FeedStatus activated,
		@RequestParam(required = false) String categoryName,
		@PageableDefault(size = 20, sort = "activeTime", direction = Sort.Direction.DESC) Pageable pageable
	) {
		FeedSearchRequest searchRequest = new FeedSearchRequest(createdAt, createBy, activated, categoryName);

		FeedResponseForAdmin feedResponse = commonFeedService.getAllFeedForAdmin(pageable, searchRequest);

		return new ApiResponse<>(feedResponse);
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
}
