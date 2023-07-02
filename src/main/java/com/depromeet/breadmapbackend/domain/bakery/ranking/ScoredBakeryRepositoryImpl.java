package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.util.List;

import org.springframework.stereotype.Repository;

/**
 * ScoredBakeryRepositoryImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
@Repository
public class ScoredBakeryRepositoryImpl implements ScoredBakeryRepository {

	@Override
	public int bulkInsert(final List<ScoredBakery> scoredBakeryList) {
		return 0;
	}
}
