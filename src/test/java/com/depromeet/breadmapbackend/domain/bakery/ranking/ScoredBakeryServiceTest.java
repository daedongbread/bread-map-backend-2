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

		final Bakery bakery1 = Bakery.builder()
			.address("수원시 영통구 삼성로 111")
			.latitude(37.5596080725671)
			.longitude(127.044235133983)
			.name("bakery")
			.status(BakeryStatus.POSTING)
			.image("bakeryImage.jpg")
			.build();
		final double bakeryRating1 = 4.5;
		final Long flagCount1 = 2L;
		final ScoredBakery scoredBakery1 = ScoredBakery.from(new BakeryScores(bakery1, bakeryRating1, flagCount1));

		final Bakery bakery2 = Bakery.builder()
			.address("달나라 달서구 달길 111")
			.latitude(37.55960807256222)
			.longitude(127.04423513333)
			.name("달나라 빵집")
			.status(BakeryStatus.POSTING)
			.image("MoonBakery.jpg")
			.build();
		final double bakeryRating2 = 5;
		final Long flagCount2 = 100L;
		final ScoredBakery scoredBakery2 = ScoredBakery.from(new BakeryScores(bakery2, bakeryRating2, flagCount2));
		repository.bulkInsert(List.of(scoredBakery1, scoredBakery2));
	}

	@Test
	void scoreBakery_등록() throws Exception {
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
		final BakeryScores bakeryScores = new BakeryScores(bakery, bakeryRating, flagCount);

		//when
		final int insertedCount = sut.registerBakeriesRank(List.of(bakeryScores));

		//then
		assertThat(insertedCount).isEqualTo(1);
	}

	@Test
	void scoreBakery_조회() throws Exception {
		//given
		final int count = 1;
		//when
		final List<ScoredBakery> result = sut.findBakeriesRankTop(count);

		//then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getBakery().getName()).isEqualTo("달나라 빵집");
		assertThat(result.get(0).getTotalScore()).isEqualTo(105L);
	}

}