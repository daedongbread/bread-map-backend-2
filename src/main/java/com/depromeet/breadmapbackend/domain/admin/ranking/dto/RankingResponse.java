package com.depromeet.breadmapbackend.domain.admin.ranking.dto;

import java.time.LocalDate;
import java.util.List;

import com.depromeet.breadmapbackend.domain.bakery.ranking.ScoredBakery;

/**
 * RankingResponse
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/18
 */
public record RankingResponse(
	String startDate,
	String endDate,
	List<String> dateList,
	List<SimpleBakeryResponse> simpleBakeryInfoList
) {

	public record SimpleBakeryResponse(
		Long id,
		int rank,
		Long bakeryId,
		String bakeryName,
		String address,
		Long viewCount,
		Long flagCount,
		Double score,
		LocalDate calculatedDate
	) {

		public SimpleBakeryResponse(final ScoredBakery scoredBakery) {
			this(scoredBakery.getId(),
				scoredBakery.getRank(),
				scoredBakery.getBakery().getId(),
				scoredBakery.getBakery().getName(),
				scoredBakery.getBakery().getAddress(),
				scoredBakery.getViewCount(),
				scoredBakery.getFlagCount(),
				scoredBakery.getTotalScore(),
				scoredBakery.getCalculatedDate()
			);
		}
	}
}
