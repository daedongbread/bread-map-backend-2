package com.depromeet.breadmapbackend.domain.bakery.ranking;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.springframework.util.Assert;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.ranking.dto.BakeryScores;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ScoredBakery
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScoredBakery {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@OneToOne
	@JoinColumn(name = "bakery_id")
	private Bakery bakery;
	private double bakeryRating;
	private Long flagCount;
	private double totalScore;
	private String createdWeekOfMonthYear;

	@Builder
	private ScoredBakery(
		final Long id,
		final Bakery bakery,
		final double bakeryRating,
		final Long flagCount,
		final double totalScore,
		final String createdWeekOfMonthYear
	) {

		Assert.notNull(bakery, "bakery must not be null");
		Assert.isTrue(bakeryRating >= 0 && bakeryRating <= 5, "bakeryRating must be between 0 and 5");
		Assert.notNull(flagCount, "flagCount must not be null");
		Assert.notNull(createdWeekOfMonthYear, "weekOfMonth must not be null");

		this.id = id;
		this.bakery = bakery;
		this.bakeryRating = bakeryRating;
		this.flagCount = flagCount;
		this.totalScore = totalScore;
		this.createdWeekOfMonthYear = createdWeekOfMonthYear;
	}

	public static ScoredBakery from(final BakeryScores bakeryScores) {
		return builder()
			.bakery(bakeryScores.bakery())
			.bakeryRating(bakeryScores.bakeryRating())
			.flagCount(bakeryScores.flagCount())
			.totalScore(calculateTotalScore(bakeryScores.bakeryRating(), bakeryScores.flagCount()))
			.createdWeekOfMonthYear(bakeryScores.weekOfMonth())
			.build();
	}

	private static double calculateTotalScore(final double bakeryRating, final Long flagCount) {
		return (bakeryRating * RankWeight.RATING_WEIGHT.getWeight()) +
			(flagCount.doubleValue() * RankWeight.FLAG_COUNT_WEIGHT.getWeight());
	}
}
