package com.depromeet.breadmapbackend.domain.admin.ranking.dto;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * RankingUpdateRequest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/18
 */
public record RankingUpdateRequest(
	@Size(min = 2) List<BakeryRankInfo> bakeryRankInfoList
) {
	public record BakeryRankInfo(
		@NotNull Long id,
		@NotNull int rank
	) {
	}
}
