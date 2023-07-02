package com.depromeet.breadmapbackend.domain.bakery.ranking;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.bakery.ranking.dto.BakeryScores;

/**
 * BakeryRankTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
class ScoredBakeryServiceTest {

	private ScoredBakeryService sut;

	@BeforeEach
	void setUp() {
		final ScoredBakeryRepository repository = new ScoredBakeryRepositoryImpl();
		sut = new ScoredBakeryServiceImpl(repository);
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
		final BakeryScores bakeryScores = new BakeryScores(bakery, bakeryRating, flagCount);

		//when
		final int insertedCount = sut.registerBakeriesRank(List.of(bakeryScores));

		//then
		assertThat(insertedCount).isEqualTo(1);

	}

}