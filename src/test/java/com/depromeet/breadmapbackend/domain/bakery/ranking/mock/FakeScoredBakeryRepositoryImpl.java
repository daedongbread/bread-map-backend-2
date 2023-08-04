package com.depromeet.breadmapbackend.domain.bakery.ranking.mock;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.test.util.ReflectionTestUtils;

import com.depromeet.breadmapbackend.domain.admin.ranking.dto.RankingResponse;
import com.depromeet.breadmapbackend.domain.admin.ranking.dto.RankingUpdateRequest;
import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.ScoredBakery;
import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.infrastructure.ScoredBakeryRepository;

/**
 * ScoredBakeryRepositoryImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
public class FakeScoredBakeryRepositoryImpl implements ScoredBakeryRepository {

	private static final AtomicLong autoGeneratedId = new AtomicLong(0);
	private static final List<ScoredBakery> data = Collections.synchronizedList(new ArrayList<>());
	private static final List<ScoredBakery> cachedData = Collections.synchronizedList(new ArrayList<>());

	@Override
	public int bulkInsert(final List<ScoredBakery> scoredBakeryList) {
		data.clear();
		scoredBakeryList.stream()
			.map(scoredBakery -> ScoredBakery.builder()
				.bakery(scoredBakery.bakery())
				.totalScore(scoredBakery.totalScore())
				.calculatedDate(scoredBakery.calculatedDate())
				.build())
			.forEach(scoredBakery -> {
				ReflectionTestUtils.setField(scoredBakery, "id", autoGeneratedId.incrementAndGet());
				scoredBakery.setRank(scoredBakeryList.indexOf(scoredBakery) + 1);
				data.add(scoredBakery);
			});

		return data.size();
	}

	@Override
	public RankingResponse findScoredBakeryByStartDate(final LocalDate startDate) {
		return null;
	}

	@Override
	public int updateRank(final RankingUpdateRequest bakeryRankInfos) {
		return 0;
	}

	@Override
	public void deleteByCalculatedDate(final LocalDate calculateDate) {

	}

	public static void prepareData(final List<ScoredBakery> scoredBakery) {
		data.addAll(scoredBakery);
	}

	public static void prepareCacheData(final List<ScoredBakery> scoredBakery) {
		cachedData.addAll(scoredBakery);
	}

	public static void clearData() {
		data.clear();
		cachedData.clear();
	}
}
