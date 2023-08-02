package com.depromeet.breadmapbackend.domain.bakery.query.domain.repository;

import java.util.List;

import com.depromeet.breadmapbackend.domain.bakery.query.domain.QueryBakeryFlagCount;

/**
 * UserBakeryFlagRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/02
 */
public interface QueryFlagBakeryRepository {
	List<Long> findByUserIdAndBakeryIdIn(Long userId, List<Long> bakeryIds);

	List<QueryBakeryFlagCount> countFlagBakeryByBakeryIdIn(List<Long> bakeryIds);
}
