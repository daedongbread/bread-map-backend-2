package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.util.List;

/**
 * ScoredBakeryRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
interface ScoredBakeryRepository {
	int bulkInsert(final List<ScoredBakery> scoredBakeryList);

	List<ScoredBakery> findBakeriesRankTop(final int count);
}
