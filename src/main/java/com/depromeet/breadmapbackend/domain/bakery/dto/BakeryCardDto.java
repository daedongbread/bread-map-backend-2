package com.depromeet.breadmapbackend.domain.bakery.dto;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.dto.MapSimpleReviewDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BakeryCardDto {
	private Double latitude;
	private Double longitude;
	private Long id;
	private String image;
	private String name;
	private int flagNum;
	private Double rating;
	private int reviewNum;
	private List<MapSimpleReviewDto> simpleReviewList;
	private Double distance;
	private int popularNum;
	private FlagColor color;

	@Builder
	public BakeryCardDto(
		final Bakery bakery,
		final int flagNum,
		final List<Review> reviewList,
		final Double distance,
		final FlagColor color
	) {
		this.latitude = bakery.getLatitude();
		this.longitude = bakery.getLongitude();
		this.id = bakery.getId();
		this.image = bakery.getImage();
		this.name = bakery.getName();
		this.flagNum = flagNum;
		this.rating = bakery.bakeryRating(reviewList);
		this.reviewNum = reviewList.size();
		this.simpleReviewList = getSimpleReviewListFrom(reviewList);
		this.distance = distance;
		this.popularNum = flagNum + reviewList.size();
		this.color = color;
	}

	private List<MapSimpleReviewDto> getSimpleReviewListFrom(final List<Review> reviewList) {
		return reviewList.stream()
			.sorted(Comparator.comparing(Review::getCreatedAt).reversed())
			.map(MapSimpleReviewDto::new)
			.limit(3)
			.collect(Collectors.toList());
	}
}
