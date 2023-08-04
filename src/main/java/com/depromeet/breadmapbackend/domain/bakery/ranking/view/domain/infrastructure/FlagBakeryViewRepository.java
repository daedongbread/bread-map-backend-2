package com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.infrastructure;

import java.util.List;

/**
 * UserBakeryFlagRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/02
 */
public interface FlagBakeryViewRepository {
	List<Long> findByUserIdAndBakeryIdIn(Long userId, List<Long> bakeryIds);
}
