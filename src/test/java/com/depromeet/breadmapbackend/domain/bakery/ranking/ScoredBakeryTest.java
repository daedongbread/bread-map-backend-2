package com.depromeet.breadmapbackend.domain.bakery.ranking;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.bakery.dto.CalculateBakeryScoreBase;

/**
 * ScoredBakeryTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
class ScoredBakeryTest {

	@Test
	void BakeryScores_가중치에_따라_점수를_계산하여_ScoredBakery를_생성할_수_있다() throws Exception {
		//given
		final Bakery bakery = Bakery.builder()
			.address("수원시 영통구 삼성로 111")
			.latitude(37.5596080725671)
			.longitude(127.044235133983)
			.name("bakery")
			.status(BakeryStatus.POSTING)
			.image("bakeryImage.jpg")
			.build();
		final double bakeryRating = 4.5;
		final Long flagCount = 2L;
		final Long viewCount = 1000L;

		final double expectedTotalScore = 1006.5;
		final CalculateBakeryScoreBase bakeryScores = new CalculateBakeryScoreBase(bakery, bakeryRating, flagCount, viewCount);
		//when
		final ScoredBakery result = ScoredBakery.from(bakeryScores, LocalDate.now());
		//then
		assertThat(result.getTotalScore()).isEqualTo(expectedTotalScore);

	}

}