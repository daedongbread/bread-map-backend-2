package com.depromeet.breadmapbackend.domain.admin.ranking.dto;

import java.time.LocalDate;
import java.util.List;

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
	List<SimpleBakeryResponse> simpleBakeryInfoList
) {

	record SimpleBakeryResponse(
		int rank,
		Long bakeryId,
		String bakeryName,
		Long viewCount,
		Long flagCount,
		Double score,
		LocalDate calculatedDate
	) {
	}
}
