package com.depromeet.breadmapbackend.domain.admin.feed.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.depromeet.breadmapbackend.domain.admin.Admin;
import com.depromeet.breadmapbackend.domain.admin.category.domain.Category;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.*;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.CurationFeedRequestDto;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.FeedRequestDto;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.CommonFeedResponseDto;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.CurationFeedResponseDto;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.FeedResponseForAdmin;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.FeedResponseForUser;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.LandingFeedResponseDto;
import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.product.Product;
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

	public static CommonFeedResponseDto toCommonDto(Feed feed) {
		return CommonFeedResponseDto.of(feed);
	}

	public static CurationFeedResponseDto toCurationDto(Bakery bakery, Product product) {
		return CurationFeedResponseDto.of(bakery, product);
	}

	public static List<CurationFeedResponseDto> toCurationDto(List<Bakery> bakeries, List<Product> products) {
		return bakeries.stream()
			.flatMap(bakery -> products.stream()
				.filter(product ->
					bakery.getId().equals(product.getBakery().getId())) // != bakery.equals(product.getBakery())
				.map(product -> toCurationDto(bakery, product))
			)
			.collect(Collectors.toList());
	}

	public static LandingFeedResponseDto toLandingDto(Feed feed) {
		return LandingFeedResponseDto.of(feed);
	}

	public static List<LandingFeedResponseDto> toLandingDto(List<Feed> feeds) {
		return feeds.stream().map(FeedAssembler::toLandingDto).collect(Collectors.toList());
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

	public static FeedResponseForAdmin toDtoForAdmin(int totalPage, long totalElement, List<Feed> feeds) {
		return FeedResponseForAdmin.of(totalPage, totalElement, feeds);
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

	public static CurationBakery toCurationBakery(CurationFeed curationFeed, Bakery bakery, CurationFeedRequestDto requestDto) {
		return new CurationBakery(curationFeed, bakery, requestDto);
	}

	public static List<CurationBakery> toCurationBakery(CurationFeed curationFeed, List<Bakery> bakeries, FeedRequestDto request) {
		return bakeries.stream()
				.map(bakery -> new CurationBakery(curationFeed, bakery, request.findCuration(bakery.getId())))
//				.forEach(this.bakeries::add);
				.collect(Collectors.toList());
	}
}
