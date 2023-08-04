package com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain;

import java.time.LocalDate;

import lombok.Builder;

/**
 * QueryBakeryRank
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/02
 */

public record BakeryRankView(
	Long id,
	int rank,
	Long bakeryId,
	LocalDate calculatedDate,
	String name,
	String image,
	double rating,
	long flagCount,
	String shortAddress,
	Integer version
) {

	@Builder
	public BakeryRankView {
	}

	public BakeryRankView updateFlagCount(final Long flagCount) {
		return
			new BakeryRankView(
				this.id,
				this.rank,
				this.bakeryId,
				this.calculatedDate,
				this.name,
				this.image,
				this.rating,
				flagCount,
				this.shortAddress,
				this.version
			);
	}

	public BakeryRankView updateRating(final double rating) {
		return
			new BakeryRankView(
				this.id,
				this.rank,
				this.bakeryId,
				this.calculatedDate,
				this.name,
				this.image,
				rating,
				this.flagCount,
				this.shortAddress,
				this.version
			);
	}

	public BakeryRankView updateRank(final int rank) {
		return
			new BakeryRankView(
				this.id,
				rank,
				this.bakeryId,
				this.calculatedDate,
				this.name,
				this.image,
				this.rating,
				this.flagCount,
				this.shortAddress,
				this.version
			);
	}
}
