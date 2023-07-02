package com.depromeet.breadmapbackend.domain.bakery.dto;

/**
 * BakeryRankingCard
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
public record BakeryRankingCard(
	String image,
	int flagNum,
	double rating,
	String shortAddress,
	boolean isFlagged
) {
}
