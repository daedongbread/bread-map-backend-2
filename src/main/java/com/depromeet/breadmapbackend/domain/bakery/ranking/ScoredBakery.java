package com.depromeet.breadmapbackend.domain.bakery.ranking;

import org.springframework.util.Assert;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.ranking.dto.BakeryScores;

import lombok.Builder;
import lombok.Getter;

/**
 * ScoredBakery
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
@Getter
public class ScoredBakery {

	private final Long id;
	private final Bakery bakery;
	private final double bakeryRating;
	private final Long flagCount;
	private final double totalScore;

	@Builder
	private ScoredBakery(final Long id, final Bakery bakery, final double bakeryRating,
		final Long flagCount, final double totalScore) {

		Assert.notNull(bakery, "bakery must not be null");
		Assert.isTrue(bakeryRating >= 0 && bakeryRating <= 5, "bakeryRating must be between 0 and 5");
		Assert.notNull(flagCount, "flagCount must not be null");

		this.id = id;
		this.bakery = bakery;
		this.bakeryRating = bakeryRating;
		this.flagCount = flagCount;
		this.totalScore = totalScore;
	}

	public static ScoredBakery from(final BakeryScores bakeryScores) {
		return builder()
			.bakery(bakeryScores.bakery())
			.bakeryRating(bakeryScores.bakeryRating())
			.flagCount(bakeryScores.flagCount())
			.totalScore(calculateTotalScore(bakeryScores.bakeryRating(), bakeryScores.flagCount()))
			.build();
	}

	private static double calculateTotalScore(final double bakeryRating, final Long flagCount) {
		return (bakeryRating * RankWeight.RATING_WEIGHT.getWeight()) +
			(flagCount.doubleValue() * RankWeight.FLAG_COUNT_WEIGHT.getWeight());
	}
}
