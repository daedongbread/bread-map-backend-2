package com.depromeet.breadmapbackend.domain.bakery.dto;

import java.time.LocalDate;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.BakeryScoreBase;

/**
 * BakeryRanking
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
public record BakeryScoreBaseWithSelectedDate(
	Bakery bakery,
	// double bakeryRating,
	Long flagCount,
	Long viewCount,
	LocalDate selectedDate
) {

	public BakeryScoreBaseWithSelectedDate(final BakeryScoreBase bakeryScoreBase, final LocalDate selectedDate) {
		this(
			bakeryScoreBase.bakery(),
			// bakeryScoreBase.bakeryRating(),
			bakeryScoreBase.flagCount(),
			bakeryScoreBase.viewCount(),
			selectedDate
		);
	}
}
