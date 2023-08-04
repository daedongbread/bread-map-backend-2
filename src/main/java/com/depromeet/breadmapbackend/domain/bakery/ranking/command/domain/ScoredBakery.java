package com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain;

import java.time.LocalDate;

import org.springframework.util.Assert;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;

import lombok.Builder;

/**
 * ScoredBakery
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
public record ScoredBakery(
	Long id,
	Bakery bakery,
	long viewCount,
	long flagCount,
	double rating,
	double totalScore,
	LocalDate calculatedDate,
	int rank
) {
	@Builder
	public ScoredBakery {
		Assert.notNull(bakery, "bakery must not be null");
		Assert.notNull(calculatedDate, "calculatedDate must not be null");
	}

	private ScoredBakery(
		final Bakery bakery,
		final long viewCount,
		final long flagCount,
		final double rating,
		final LocalDate calculatedDate
	) {
		this(null, bakery, viewCount, flagCount, rating, calculateTotalScore(flagCount, viewCount), calculatedDate, 0);
	}

	public ScoredBakery setRank(final int rank) {
		return new ScoredBakery(id, bakery, viewCount, flagCount, rating, totalScore, calculatedDate, rank);
	}

	private static double calculateTotalScore(
		final Long flagCount,
		final Long viewCount
	) {
		return (flagCount.doubleValue() * RankWeight.FLAG_COUNT_WEIGHT.getWeight()) +
			(viewCount.doubleValue() * RankWeight.VIEW_COUNT_WEIGHT.getWeight());
	}
}
