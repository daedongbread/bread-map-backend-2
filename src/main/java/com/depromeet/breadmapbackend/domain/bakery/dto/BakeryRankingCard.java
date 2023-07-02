package com.depromeet.breadmapbackend.domain.bakery.dto;

import lombok.Builder;

/**
 * BakeryRankingCard
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */

@Builder
public record BakeryRankingCard(
	Long id,
	String name,
	String image,
	Long flagNum,
	double rating,
	String shortAddress,
	boolean isFlagged
) {
}
