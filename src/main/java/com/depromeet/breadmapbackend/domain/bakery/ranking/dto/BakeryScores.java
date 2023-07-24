package com.depromeet.breadmapbackend.domain.bakery.ranking.dto;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;

/**
 * BakeryScores
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
public record BakeryScores(
	Bakery bakery,
	double bakeryRating,
	Long flagCount,
	String weekOfMonth
) {

}
