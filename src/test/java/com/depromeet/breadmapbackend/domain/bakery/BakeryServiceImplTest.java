package com.depromeet.breadmapbackend.domain.bakery;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryRankingCard;

/**
 * BakeryServiceImplTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */

class BakeryServiceImplTest extends BakeryServiceTest {

	@Autowired
	private BakeryServiceImpl sut;

	@Test
	void 인기_빵집_랭킹_조회하면_기대하는_응답을_반환한다() throws Exception {
		final List<BakeryRankingCard> bakeryRankingCardList = sut.getBakeryRankingTop(5, 1L);
	}
}