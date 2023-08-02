package com.depromeet.breadmapbackend.domain.bakery.query.domain;

/**
 * QueryBakeryFlagCount
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/02
 */
public record QueryBakeryFlagCount(
	Long bakeryId,
	Long flagCount
) {
}
