package com.depromeet.breadmapbackend.domain.admin.ranking.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * RankingUpdateRequest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/18
 */
public record RankingUpdateRequest(

	LocalDate date,
	List<BakeryRankInfo> bakeryRankInfoList
) {
	public record BakeryRankInfo(
		Long id,
		int rank,
		Long bakeryId
	) {
	}
}
