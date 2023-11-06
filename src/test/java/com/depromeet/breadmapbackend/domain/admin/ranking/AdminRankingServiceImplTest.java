package com.depromeet.breadmapbackend.domain.admin.ranking;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import com.depromeet.breadmapbackend.domain.admin.ranking.dto.RankingResponse;
import com.depromeet.breadmapbackend.domain.admin.ranking.dto.RankingUpdateRequest;
import com.depromeet.breadmapbackend.domain.bakery.ranking.ScoredBakery;
import com.depromeet.breadmapbackend.utils.TestLocalStackConfig;

/**
 * AdminRankingServiceImplTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/19
 */
@SpringBootTest
@Import({TestLocalStackConfig.class})
class AdminRankingServiceImplTest {

	@Autowired
	private AdminRankingService sut;
	@Autowired
	private EntityManager em;

	@Test
	@Sql("classpath:admin-rank-test-data.sql")
	void 랭킹_조회() throws Exception {
		//given
		final String startDate = "2023-07-07";

		//when
		final RankingResponse result = sut.getRanking(startDate);

		//then
		assertThat(result.startDate()).isEqualTo("2023-07-07");
		assertThat(result.endDate()).isEqualTo("2023-07-13");
		assertThat(result.dateList())
			.containsExactly(
				"2023-07-07",
				"2023-07-08",
				"2023-07-09",
				"2023-07-10",
				"2023-07-11",
				"2023-07-13"
			);
	}

	@Test
	@Sql("classpath:admin-rank-test-data.sql")
	void 랭킹_수정() throws Exception {
		//given
		final List<RankingUpdateRequest.BakeryRankInfo> bakeryRankInfos = List.of(
			new RankingUpdateRequest.BakeryRankInfo(
				111L, 2
			),
			new RankingUpdateRequest.BakeryRankInfo(
				115L, 1
			),
			new RankingUpdateRequest.BakeryRankInfo(
				112L, 5
			)
		);

		//when
		final int result = sut.updateRanking(new RankingUpdateRequest(bakeryRankInfos));

		//then
		assertThat(result).isEqualTo(3);
		assertThat(em.find(ScoredBakery.class, 111L).getRank()).isEqualTo(2);
		assertThat(em.find(ScoredBakery.class, 115L).getRank()).isEqualTo(1);
		assertThat(em.find(ScoredBakery.class, 112L).getRank()).isEqualTo(5);
	}
}