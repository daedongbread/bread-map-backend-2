package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.springframework.util.Assert;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.dto.CalculateBakeryScoreBase;

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
	private double totalScore;
	private LocalDate calculatedDate;

	@Builder
	private ScoredBakery(
		final Bakery bakery,
		final double totalScore,
		final LocalDate calculatedDate
	) {

		Assert.notNull(bakery, "bakery must not be null");
		Assert.notNull(calculatedDate, "calculatedDate must not be null");

		this.bakery = bakery;
		this.totalScore = totalScore;
		this.calculatedDate = calculatedDate;
	}

	public static ScoredBakery from(final CalculateBakeryScoreBase bakeryScoreBase, final LocalDate calculatedDate) {
		return builder()
			.bakery(bakeryScoreBase.bakery())
			.totalScore(calculateTotalScore(bakeryScoreBase.bakeryRating(), bakeryScoreBase.flagCount(), bakeryScoreBase.viewCount()))
			.calculatedDate(calculatedDate)
			.build();
	}

	private static double calculateTotalScore(final double bakeryRating, final Long flagCount, final Long viewCount) {
		return (bakeryRating * RankWeight.RATING_WEIGHT.getWeight()) +
			(flagCount.doubleValue() * RankWeight.FLAG_COUNT_WEIGHT.getWeight()) +
			(viewCount.doubleValue() * RankWeight.VIEW_COUNT_WEIGHT.getWeight());
	}
}
