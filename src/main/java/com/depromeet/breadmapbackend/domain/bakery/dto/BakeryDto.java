package com.depromeet.breadmapbackend.domain.bakery.dto;

import java.util.List;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.user.User;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BakeryDto {
	private BakeryInfo bakeryInfo;
	private PioneerInfo pioneerInfo;
	private FlagInfo flagInfo;
	private List<FacilityInfo> facilityInfoList;

	@Builder
	public BakeryDto(
		final Bakery bakery,
		final int flagCount,
		final List<Review> reviewList,
		final FlagBakery userFlagBakery
	) {
		this.bakeryInfo = BakeryInfo.builder()
			.bakery(bakery)
			.flagNum(flagCount)
			.reviewList(reviewList)
			.build();
		this.pioneerInfo = PioneerInfo.builder()
			.pioneer(bakery.getPioneer())
			.build();
		this.flagInfo = FlagInfo.builder()
			.flagBakery(userFlagBakery)
			.build();
		this.facilityInfoList = bakery.getFacilityInfoList();
	}

	@Getter
	@NoArgsConstructor
	public static class BakeryInfo {
		private List<String> images;
		private String name;
		private Integer flagNum;
		private Double rating;
		private Integer reviewNum;
		private String address;
		private String hours;
		private String websiteURL;
		private String instagramURL;
		private String facebookURL;
		private String blogURL;
		private String phoneNumber;
		private String checkPoint;
		private String newBreadTime;

		@Builder
		public BakeryInfo(Bakery bakery, Integer flagNum, List<Review> reviewList) {
			this.images = bakery.getImages();
			this.name = bakery.getName();
			this.flagNum = flagNum;
			this.rating = bakery.bakeryRating(reviewList);
			this.reviewNum = reviewList.size();
			this.address = bakery.getFullAddress();
			this.hours = bakery.getHours();
			this.websiteURL = bakery.getBakeryURL().getWebsiteURL();
			this.instagramURL = bakery.getBakeryURL().getInstagramURL();
			this.facebookURL = bakery.getBakeryURL().getFacebookURL();
			this.blogURL = bakery.getBakeryURL().getBlogURL();
			this.phoneNumber = bakery.getPhoneNumber();
			this.checkPoint = bakery.getCheckPoint();
			this.newBreadTime = bakery.getNewBreadTime();
		}
	}

	@Getter
	@NoArgsConstructor
	public static class FlagInfo {
		private Long flagId;
		private Boolean isFlaged;

		@Builder
		public FlagInfo(FlagBakery flagBakery) {
			this.flagId = flagBakery != null ? flagBakery.getFlag().getId() : null;
			this.isFlaged = flagBakery != null;
		}
	}

	@Getter
	@NoArgsConstructor
	public static class PioneerInfo {
		private Long pioneerId;
		private String pioneerNickName;

		@Builder
		public PioneerInfo(User pioneer) {
			this.pioneerId = pioneer == null ? null : pioneer.getId();
			this.pioneerNickName = pioneer == null ? null : pioneer.getNickName();
		}
	}
}
