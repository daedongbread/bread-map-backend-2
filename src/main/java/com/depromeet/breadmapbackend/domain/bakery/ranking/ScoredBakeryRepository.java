package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.time.LocalDate;
import java.util.List;

import com.depromeet.breadmapbackend.domain.admin.ranking.dto.RankingResponse;
import com.depromeet.breadmapbackend.domain.admin.ranking.dto.RankingUpdateRequest;

/**
 * ScoredBakeryRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
public interface ScoredBakeryRepository {
	int bulkInsert(final List<ScoredBakery> scoredBakeryList);

	List<ScoredBakery> findScoredBakeryByCalculatedDate(final LocalDate calculatedDate, final int size);

	List<RankingResponse> findScoredBakeryByStartDate(final LocalDate startDate);

	int updateRank(RankingUpdateRequest request);
}
