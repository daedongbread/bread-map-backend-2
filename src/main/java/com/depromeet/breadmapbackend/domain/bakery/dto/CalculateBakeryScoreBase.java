package com.depromeet.breadmapbackend.domain.bakery.dto;

import java.time.LocalDate;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;

/**
 * BakeryRanking
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
public record CalculateBakeryScoreBase(
	Bakery bakery,
	double bakeryRating,
	Long flagCount,
	Long viewCount,
	LocalDate calculatedDate
) {
}
