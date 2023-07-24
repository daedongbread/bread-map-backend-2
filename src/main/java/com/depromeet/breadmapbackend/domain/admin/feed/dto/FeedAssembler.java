package com.depromeet.breadmapbackend.domain.admin.feed.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.depromeet.breadmapbackend.domain.admin.Admin;
import com.depromeet.breadmapbackend.domain.admin.category.domain.Category;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.CurationFeed;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.Feed;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedType;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.LandingFeed;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.FeedRequestDto;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.FeedResponseDto;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.FeedResponseForAdmin;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.FeedResponseForUser;
import com.depromeet.breadmapbackend.global.converter.DateTimeParser;

public class FeedAssembler {

	private FeedAssembler() {
	}

	public static Feed toEntity(Admin admin, Category category, FeedRequestDto requestDto) {

		return switch (requestDto.getCommon().getFeedType()) {
			case LANDING -> toLanding(admin, category, requestDto);
			case CURATION -> toCuration(admin, category, requestDto);
		};
	}

	public static FeedResponseDto toDto(Feed feed) {
		return switch (FeedType.findByCode(feed.getFeedType())) {
			case LANDING -> toLandingDto(feed);
			case CURATION -> toCurationDto(feed);
		};
	}

	public static List<FeedResponseDto> toDto(List<Feed> feeds) {
		return feeds.stream()
			.map(FeedAssembler::toDto)
			.collect(Collectors.toList());
	}

	private static FeedResponseDto toCurationDto(Feed feed) {
		return FeedResponseDto.ofCuration(feed);
	}

	private static FeedResponseDto toLandingDto(Feed feed) {
		return FeedResponseDto.ofLanding(feed);
	}

	private static CurationFeed toCuration(Admin admin, Category category, FeedRequestDto requestDto) {
		return CurationFeed.builder()
			.admin(admin)
			.subTitle(requestDto.getCommon().getSubTitle())
			.category(category)
			.introduction(requestDto.getCommon().getIntroduction())
			.conclusion(requestDto.getCommon().getConclusion())
			.thumbnailUrl(requestDto.getCommon().getThumbnailUrl())
			.feedType(requestDto.getCommon().getFeedType().toString())
			.like(0)
			.activated(requestDto.getCommon().getActivated())
			.activeTime(DateTimeParser.parse(requestDto.getCommon().getActiveTime()))
			.build();
	}

	private static LandingFeed toLanding(Admin admin, Category category, FeedRequestDto requestDto) {
		return LandingFeed.builder()
			.admin(admin)
			.subTitle(requestDto.getCommon().getSubTitle())
			.category(category)
			.introduction(requestDto.getCommon().getIntroduction())
			.conclusion(requestDto.getCommon().getConclusion())
			.thumbnailUrl(requestDto.getCommon().getThumbnailUrl())
			.feedType(requestDto.getCommon().getFeedType().toString())
			.activated(requestDto.getCommon().getActivated())
			.activeTime(DateTimeParser.parse(requestDto.getCommon().getActiveTime()))
			.redirectUrl(requestDto.getLanding().getRedirectUrl())
			.build();
	}

	public static List<FeedResponseForAdmin> toDtoForAdmin(List<Feed> feeds) {
		return feeds.stream().map(FeedAssembler::toDtoForAdmin).collect(Collectors.toList());
	}

	public static FeedResponseForAdmin toDtoForAdmin(Feed feed) {
		return FeedResponseForAdmin.of(feed);
	}

	public static FeedResponseForUser curationToDto(Feed feed) {
		return FeedResponseForUser.builder()
			.feedId(feed.getId())
			.imageUrl(feed.getThumbnailUrl())
			.feedType(feed.getFeedType())
			.build();
	}

	public static FeedResponseForUser feedToDto(Feed feed) {

		LandingFeed landingFeed = (LandingFeed)feed;

		return FeedResponseForUser.builder()
			.feedId(feed.getId())
			.imageUrl(feed.getThumbnailUrl())
			.feedType(feed.getFeedType())
			.redirectUrl(landingFeed.getRedirectUrl())
			.build();
	}

	public static FeedResponseForUser toDtoForUser(Feed feed) {
		return switch (FeedType.findByCode(feed.getFeedType())) {
			case LANDING -> feedToDto(feed);
			case CURATION -> curationToDto(feed);
		};
	}

	public static List<FeedResponseForUser> toDtoForUser(List<Feed> feeds) {
		return feeds.stream()
			.map(FeedAssembler::toDtoForUser)
			.collect(Collectors.toList());
	}
}
