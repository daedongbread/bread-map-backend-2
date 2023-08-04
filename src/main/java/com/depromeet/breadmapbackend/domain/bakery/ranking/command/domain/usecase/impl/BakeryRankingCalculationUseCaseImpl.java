package com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.usecase.impl;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.BakeryScoreBase;
import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.ScoredBakery;
import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.infrastructure.BakeryBaseScoreRepository;
import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.infrastructure.BakeryRankingCalculationDoneEvent;
import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.infrastructure.RedisRepository;
import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.infrastructure.ScoredBakeryRepository;
import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.usecase.BakeryRankingCalculationUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * BakeryRankingCalculationUseCaseImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/03
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class BakeryRankingCalculationUseCaseImpl implements BakeryRankingCalculationUseCase {
	private static final int RANK_LIMIT = 40;

	private final ScoredBakeryRepository scoredBakeryRepository;
	private final BakeryBaseScoreRepository bakeryBaseScoreRepository;
	private final RedisRepository redisRepository;
	private final BakeryRankingCalculationDoneEvent bakeryRankingCalculationDoneEvent;

	@Override
	public void command(
		final String EVENT_KEY,
		final LocalDate calculateDate
	) {
		if (isFirstInstanceToCalculateRanks(EVENT_KEY)) {
			log.info("This instance is first instance to calculate ranking");

			final List<BakeryScoreBase> bakeriesScoreFactors = getBakeryScores(calculateDate);
			log.info("bakeriesScoreFactors: {}", bakeriesScoreFactors.size());

			final List<ScoredBakery> scoredSortedBakeryList =
				calculateBakeryRankingAndSort(bakeriesScoreFactors, calculateDate);

			final List<ScoredBakery> topRankBakeryList = getTopRankBakeryList(scoredSortedBakeryList);

			log.info("The calculation is done");
			saveTopRankBakeryList(topRankBakeryList);
			bakeryRankingCalculationDoneEvent.publish(calculateDate);
		}
	}

	private List<ScoredBakery> calculateBakeryRankingAndSort(
		final List<BakeryScoreBase> bakeriesScoreFactors,
		final LocalDate localDate
	) {
		return bakeriesScoreFactors
			.stream()
			.map(bakeryScoreBase -> bakeryScoreBase.toDomain(localDate))
			.sorted(
				Comparator.comparing(ScoredBakery::totalScore)
					.thenComparing(scoredBakery -> scoredBakery.bakery().getId()).reversed()
			)
			.toList();
	}

	private boolean isFirstInstanceToCalculateRanks(final String EVENT_KEY) {

		final Optional<Long> incrementedValue = redisRepository.increment(EVENT_KEY);
		return incrementedValue.isPresent() && incrementedValue.get() == 1L;
	}

	private List<BakeryScoreBase> getBakeryScores(final LocalDate calculateDate) {
		return bakeryBaseScoreRepository.getBakeriesScoreFactors(calculateDate);
	}

	private int saveTopRankBakeryList(final List<ScoredBakery> scoredBakeryList) {
		return scoredBakeryRepository.bulkInsert(scoredBakeryList);
		// TODO : rank view 변경 이벤트 발행 -> ( 랭킹뷰 재생성  기본적인 빵집 정보만 저장 )
		// TODO : 빵집 깃발 카운트 평정 출력해야하는데.....사용자 깃발 추가 여부, 평점 이벤트 발행해 말아??? 평점은... 안해도???
	}

	private List<ScoredBakery> getTopRankBakeryList(final List<ScoredBakery> scoredBakeryList) {
		final int rankLimit = Math.min(RANK_LIMIT, scoredBakeryList.size());
		final List<ScoredBakery> scoredBakeries = scoredBakeryList.subList(0, rankLimit);

		int rank = 1;
		for (final ScoredBakery scoredBakery : scoredBakeries) {
			scoredBakery.setRank(rank++);
		}
		return scoredBakeries;
	}

}
