package com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.usecase;

import java.time.LocalDate;
import java.util.List;

/**
 * QueryBakeryRankUseCase
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/02
 */
public interface BakeryRankViewUseCase {
	List<Query> query(Long userId, int size);

	record Query(
		Long id,
		String name,
		String image,
		Long flagNum,
		double rating,
		String shortAddress,
		boolean isFlagged,
		LocalDate calculatedDate
	) {
	}
}
