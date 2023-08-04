package com.depromeet.breadmapbackend.domain.bakery.ranking.view.interfaces;

import java.util.List;

import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.usecase.BakeryRankViewUseCase;

/**
 * Mapper
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/03
 */
public class Mapper {
	public static List<BakeryRankingCardResponse> of(final List<BakeryRankViewUseCase.Query> query) {
		return query.stream()
			.map(q ->
				new BakeryRankingCardResponse(
					q.id(),
					q.name(),
					q.image(),
					q.flagNum(),
					q.rating(),
					q.shortAddress(),
					q.isFlagged(),
					q.calculatedDate()
				)
			)
			.toList();
	}
}
