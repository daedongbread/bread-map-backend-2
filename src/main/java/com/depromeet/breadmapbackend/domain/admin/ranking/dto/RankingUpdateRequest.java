package com.depromeet.breadmapbackend.domain.admin.ranking.dto;

import java.util.List;

/**
 * RankingUpdateRequest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/18
 */
public record RankingUpdateRequest(
	List<BakeryRankInfo> bakeryRankInfoList
) {
	public record BakeryRankInfo(
		Long id,
		int rank
	) {
	}
}
