package com.depromeet.breadmapbackend.domain.bakery.ranking;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.bakery.ranking.dto.BakeryRankingCard;
import com.depromeet.breadmapbackend.domain.bakery.ranking.dto.BakeryScores;

/**
 * BakeryRankTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
class ScoredBakeryServiceImplTest extends ScoredBakeryServiceTest {

	@Autowired
	private ScoredBakeryServiceImpl sut;

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
		final List<ScoredBakery> result = em.createQuery("select sb from ScoredBakery sb",
				ScoredBakery.class)
			.getResultList();
		assertThat(insertedCount).isEqualTo(1);
		assertThat(result.size()).isEqualTo(1);
		assertThat(result.get(0).getTotalScore()).isEqualTo(6.5);

	}

	@Test
	@Sql("classpath:scoredBakery-test-data.sql")
	void 인기_빵집_랭킹_조회하면_기대하는_응답을_반환한다() throws Exception {
		// given
		final Long userId = 111L;
		final int searchSize = 5;
		final List<Long> rankingIdList = List.of(600L, 500L, 100L, 200L, 300L);

		// when
		final List<BakeryRankingCard> bakeryRankingCardList = sut.findBakeriesRankTop(userId, searchSize);

		// then
		assertThat(bakeryRankingCardList.size()).isEqualTo(5);
		assertThat(bakeryRankingCardList.stream().map(BakeryRankingCard::id).toList())
			.containsExactlyElementsOf(rankingIdList);
		assertThat(bakeryRankingCardList.get(3).rating()).isEqualTo(4.9);
		assertThat(bakeryRankingCardList.get(0).shortAddress()).isEqualTo("서울 강동구");
		assertThat(bakeryRankingCardList.get(0).isFlagged()).isTrue();
		assertThat(bakeryRankingCardList.get(1).isFlagged()).isFalse();
		assertThat(bakeryRankingCardList.get(2).isFlagged()).isTrue();
	}

}