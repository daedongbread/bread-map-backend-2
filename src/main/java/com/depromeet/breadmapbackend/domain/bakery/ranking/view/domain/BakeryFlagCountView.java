package com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain;

/**
 * QueryBakeryFlagCount
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/02
 */
public record BakeryFlagCountView(
	Long bakeryId,
	Long flagCount
) {
}
