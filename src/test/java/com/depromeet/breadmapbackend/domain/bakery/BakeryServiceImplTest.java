package com.depromeet.breadmapbackend.domain.bakery;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryRankingCard;

/**
 * BakeryServiceImplTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */

// flag bakery
// 100,
// 	200,
// 	300,
// 	400,
// 	500,
// 	600,

// rating
// 100 - 3
// 300 - 4
// 700 - 2.5

class BakeryServiceImplTest extends BakeryServiceTest {

	@Autowired
	private BakeryServiceImpl sut;

	@Test
	@Sql("classpath:bakery-test-data.sql")
	void 인기_빵집_랭킹_조회하면_기대하는_응답을_반환한다() throws Exception {
		// given
		final Long userId = 111L;
		final int searchSize = 5;
		final List<Long> rankingIdList = List.of(600L, 500L, 100L, 200L, 300L);

		// when
		final List<BakeryRankingCard> bakeryRankingCardList = sut.getBakeryRankingTop(searchSize, userId);

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