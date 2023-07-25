package com.depromeet.breadmapbackend.domain.bakery.ranking.service;

import static com.depromeet.breadmapbackend.global.EventInfo.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.LongStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryScoreBaseWithSelectedDate;
import com.depromeet.breadmapbackend.domain.bakery.ranking.ScoredBakery;
import com.depromeet.breadmapbackend.domain.bakery.ranking.ScoredBakeryEventStream;
import com.depromeet.breadmapbackend.domain.bakery.ranking.ScoredBakeryRepository;
import com.depromeet.breadmapbackend.domain.bakery.ranking.ScoredBakeryService;
import com.depromeet.breadmapbackend.domain.bakery.ranking.ScoredBakeryServiceImpl;
import com.depromeet.breadmapbackend.domain.bakery.ranking.dto.BakeryRankingCard;
import com.depromeet.breadmapbackend.domain.bakery.ranking.mock.FakeFlagBakeryRepositoryImpl;
import com.depromeet.breadmapbackend.domain.bakery.ranking.mock.FakeScoredBakeryEventStreamImpl;
import com.depromeet.breadmapbackend.domain.bakery.ranking.mock.FakeScoredBakeryRepositoryImpl;
import com.depromeet.breadmapbackend.domain.bakery.ranking.util.FixtureFactory;
import com.depromeet.breadmapbackend.domain.flag.FlagBakeryRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

/**
 * BakeryRankTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */

class ScoredBakeryServiceImplTest {

	private ScoredBakeryService sut;
	private ScoredBakeryEventStream scoredBakeryEventStream;
	private ScoredBakeryRepository scoredBakeryRepository;
	private FlagBakeryRepository flagBakeryRepository;

	@BeforeEach
	void setUp() {
		scoredBakeryRepository = new FakeScoredBakeryRepositoryImpl();
		flagBakeryRepository = new FakeFlagBakeryRepositoryImpl();
		scoredBakeryEventStream = new FakeScoredBakeryEventStreamImpl();
		sut = new ScoredBakeryServiceImpl(scoredBakeryRepository, flagBakeryRepository, scoredBakeryEventStream);
		FakeScoredBakeryRepositoryImpl.clearData();
		FakeScoredBakeryEventStreamImpl.clearData();
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
		final Long viewCount = 100L;
		final LocalDate calculatedDate = LocalDate.now();
		final BakeryScoreBaseWithSelectedDate bakeryScoreBaseWithSelectedDate =
			new BakeryScoreBaseWithSelectedDate(bakery, flagCount, viewCount, calculatedDate);

		//when
		final int insertedCount = sut.calculateBakeryScore(List.of(bakeryScoreBaseWithSelectedDate));

		//then
		assertThat(insertedCount).isEqualTo(1);
	}

	@Test
	void 캐싱된_데이터가_있을때_인기_빵집_랭킹_조회하면_기대하는_응답을_반환한다() throws Exception {
		// given
		final List<ScoredBakery> preparedData = prepareCacheData();
		final int count = 3;
		final Long userId = 1L;
		//when
		final List<BakeryRankingCard> result = sut.findBakeriesRankTop(userId, count);

		//then
		assertThat(result).hasSize(3);
		assertThat(result.get(0).name()).isEqualTo(preparedData.get(0).getBakery().getName());
		assertThat(result.get(0).isFlagged()).isTrue();
		assertThat(result.get(1).isFlagged()).isFalse();
	}

	@Test
	void 캐시_DB_하루전_랭킹_데이터_모두_데이터가_없을때_인기_빵집_랭킹_조회하면_기대하는_응답을_반환한다() throws Exception {
		// given
		final int count = 1;
		final Long userId = 1L;

		//when
		//then
		final Throwable thrown = catchThrowable(() -> sut.findBakeriesRankTop(userId, count));
		assertThat(thrown).isInstanceOf(DaedongException.class);
		assertThat(((DaedongException)thrown).getDaedongStatus())
			.isEqualTo(DaedongStatus.CALCULATING_BAKERY_RANKING);

		final HashMap<String, String> fieldMap = FakeScoredBakeryEventStreamImpl.getFieldMap();
		assertThat(fieldMap).hasSize(1);
		assertThat(fieldMap.get(CALCULATE_RANKING_EVENT.name())).isNotNull();
	}

	@Test
	void 캐시_DB_모두_데이터가_없고_하루전_랭킹_데이터만_있을때_인기_빵집_랭킹_조회하면_기대하는_응답을_반환한다() throws Exception {
		// given
		final List<ScoredBakery> preparedData = prepareYesterdayData();
		final int count = 3;
		final Long userId = 1L;
		//when
		final List<BakeryRankingCard> result = sut.findBakeriesRankTop(userId, count);

		//then
		assertThat(result).hasSize(3);
		assertThat(result.get(0).name()).isEqualTo(preparedData.get(0).getBakery().getName());
		assertThat(result.get(0).isFlagged()).isTrue();
		assertThat(result.get(1).isFlagged()).isFalse();

		final HashMap<String, String> fieldMap = FakeScoredBakeryEventStreamImpl.getFieldMap();
		assertThat(fieldMap).hasSize(1);
		assertThat(fieldMap.get(CALCULATE_RANKING_EVENT.name())).isNotNull();
	}

	private List<ScoredBakery> prepareCacheData() {
		final List<ScoredBakery> preparedDate = LongStream.range(1, 10)
			.mapToObj(i -> FixtureFactory.getScoredBakery(i).nextObject(ScoredBakery.class))
			.sorted(Comparator.comparing(ScoredBakery::getTotalScore).reversed().thenComparing(ScoredBakery::getId))
			.toList();
		FakeScoredBakeryRepositoryImpl.prepareData(preparedDate);
		return preparedDate;
	}

	private List<ScoredBakery> prepareYesterdayData() {
		final List<ScoredBakery> preparedDate = LongStream.range(1, 10)
			.mapToObj(i -> FixtureFactory.getYesterdayScoredBakery(i).nextObject(ScoredBakery.class))
			.sorted(Comparator.comparing(ScoredBakery::getTotalScore).reversed().thenComparing(ScoredBakery::getId))
			.toList();
		FakeScoredBakeryRepositoryImpl.prepareData(preparedDate);
		return preparedDate;
	}

}