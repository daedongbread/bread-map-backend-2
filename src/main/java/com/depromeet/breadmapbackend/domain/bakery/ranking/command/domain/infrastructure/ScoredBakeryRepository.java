package com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.infrastructure;

import java.time.LocalDate;
import java.util.List;

import com.depromeet.breadmapbackend.domain.admin.ranking.dto.RankingResponse;
import com.depromeet.breadmapbackend.domain.admin.ranking.dto.RankingUpdateRequest;
import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.ScoredBakery;

/**
 * ScoredBakeryRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
public interface ScoredBakeryRepository {
	int bulkInsert(final List<ScoredBakery> scoredBakeryList);

	RankingResponse findScoredBakeryByStartDate(final LocalDate startDate);

	int updateRank(RankingUpdateRequest request);

	void deleteByCalculatedDate(LocalDate calculateDate);

}
