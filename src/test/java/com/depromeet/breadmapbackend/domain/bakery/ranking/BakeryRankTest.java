package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;

/**
 * BakeryRankTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
class BakeryRankTest {

	private BakeryRankService sut;

	@BeforeEach
	void setUp() {
		sut = new BakeryRankService();
	}

	@Test
	void bakery랭킹_등록() throws Exception {
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
		final Long reviewCount = 10L;
		final Long flagCount = 2L;
		final BakeryScores bakeryScores = new BakeryScores(bakery, bakeryRating, reviewCount, flagCount);

		//when
		sut.registerBakeriesRank(List.of(bakeryScores));

		//then

	}

	private class BakeryRankService {

		public void registerBakeriesRank(final List<BakeryScores> bakeriesScores) {
			throw new IllegalStateException("BakeryRankService::registerBakeriesRank not implemented yet");
		}
	}

	private record BakeryScores(
		Bakery bakery,
		double bakeryRating,
		Long reviewCount,
		Long flagCount
	) {

		private BakeryScores {
			Assert.notNull(bakery, "bakery must not be null");
			Assert.isTrue(bakeryRating >= 0 && bakeryRating <= 5, "bakeryRating must be between 0 and 5");
			Assert.notNull(reviewCount, "reviewCount must not be null");
			Assert.notNull(flagCount, "flagCount must not be null");
		}
	}
}