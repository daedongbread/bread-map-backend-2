package com.depromeet.breadmapbackend.domain.bakery.ranking.query.domain;

import java.time.LocalDate;

/**
 * QueryBakeryRank
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/02
 */

public record QueryBakeryRank(
	Long bakeryId,
	LocalDate calculatedDate,
	String name,
	String image,
	double rating,
	String shortAddress
) {
}
