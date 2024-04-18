package com.depromeet.breadmapbackend.domain.flag.dto;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.domain.review.dto.MapSimpleReviewDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class FlagBakeryDto {
	private FlagInfo flagInfo;
	private List<FlagBakeryInfo> flagBakeryInfoList;

	@Getter
	@NoArgsConstructor
	public static class FlagBakeryInfo {
		private Long id;
		private String image;
		private String name;
		private Integer flagNum;
		private Double rating;
		private Integer reviewNum;
		private List<MapSimpleReviewDto> simpleReviewList;

		@Builder
		public FlagBakeryInfo(Bakery bakery, Integer flagNum, Double rating, Integer reviewNum,
			List<MapSimpleReviewDto> simpleReviewList) {
			this.id = bakery.getId();
			this.image = bakery.getImages().isEmpty() ? null : bakery.getImages().get(0);
			this.name = bakery.getName();
			this.flagNum = flagNum;
			this.rating = rating;
			this.reviewNum = reviewNum;
			this.simpleReviewList = simpleReviewList;
		}
	}

	@Builder
	public FlagBakeryDto(Flag flag, List<FlagBakeryInfo> flagBakeryInfoList) {
		this.flagInfo = FlagInfo.builder().flag(flag).build();
		this.flagBakeryInfoList = flagBakeryInfoList;
	}
}
