package com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain;

import java.time.LocalDate;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;

/**
 * BakeryRanking
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
public record BakeryScoreBase(
	Bakery bakery,
	double bakeryRating,
	Long flagCount,
	Long viewCount
) {

	public ScoredBakery toDomain(
		final LocalDate calculatedDate
	) {
		final ScoredBakery build = ScoredBakery.builder()
			.bakery(this.bakery)
			.viewCount(this.viewCount)
			.flagCount(this.flagCount)
			.rating(this.bakeryRating)
			.calculatedDate(calculatedDate)
			.build();
		return build;
	}
}
