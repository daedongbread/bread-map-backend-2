package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.util.List;

/**
 * ScoredBakeryRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
public interface ScoredBakeryRepository {
	int bulkInsert(final List<ScoredBakery> scoredBakeryList, final String weekOfMonthYear);

	List<ScoredBakery> findCachedScoredBakeryByWeekOfMonthYear(final String weekOfMonthYear, final int size);

	List<ScoredBakery> findScoredBakeryByWeekOfMonthYear(final String weekOfMonthYear, final int size);
}
