package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryScoreBaseWithSelectedDate;
import com.depromeet.breadmapbackend.domain.bakery.ranking.dto.BakeryRankingCard;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
import com.depromeet.breadmapbackend.domain.flag.FlagBakeryRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ScoredBakeryServiceImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ScoredBakeryServiceImpl implements ScoredBakeryService {

	private static final int RANK_LIMIT = 40;

	private final ScoredBakeryRepository scoredBakeryRepository;
	private final FlagBakeryRepository flagBakeryRepository;
	private final ScoredBakeryEventStream scoredBakeryEventStream;

	@Transactional
	public int calculateBakeryScore(final List<BakeryScoreBaseWithSelectedDate> bakeryScoreBaseList) {
		return scoredBakeryRepository.bulkInsert(
			rankTopScoredBakeries(bakeryScoreBaseList)
		);
	}

	private List<ScoredBakery> rankTopScoredBakeries(final List<BakeryScoreBaseWithSelectedDate> bakeryScoreBaseList) {
		final List<ScoredBakery> sortedBakeryRank = sortBakeriesByScore(bakeryScoreBaseList);
		final int rankLimit = Math.min(RANK_LIMIT, sortedBakeryRank.size());
		final List<ScoredBakery> scoredBakeries = sortedBakeryRank.subList(0, rankLimit);

		int rank = 1;
		for (final ScoredBakery scoredBakery : scoredBakeries) {
			scoredBakery.setRank(rank++);
		}
		return scoredBakeries;
	}

	private List<ScoredBakery> sortBakeriesByScore(final List<BakeryScoreBaseWithSelectedDate> bakeryScoreBaseList) {
		return bakeryScoreBaseList
			.stream()
			.map(ScoredBakery::from)
			.sorted(
				Comparator.comparing(ScoredBakery::getTotalScore)
					.thenComparing(scoredBakery -> scoredBakery.getBakery().getId()).reversed()
			)
			.toList();
	}

	@Override
	public List<BakeryRankingCard> findBakeriesRankTop(final Long userId, final int size) {
		final List<ScoredBakery> scoredBakeries = findScoredBakeryBy(LocalDate.now(), size);
		final List<FlagBakery> userFlaggedBakeries = findFlagBakeryBy(userId, scoredBakeries);

		return scoredBakeries.stream()
			.map(bakeryScores -> from(userFlaggedBakeries, bakeryScores))
			.limit(size)
			.toList();
	}

	private List<ScoredBakery> findScoredBakeryBy(final LocalDate calculatedDate, final int size) {
		log.info("findScoredBakeryBy calculatedDate: {}, size: {}", calculatedDate, size);
		final List<ScoredBakery> ranksFromDb = getRanksFromDb(calculatedDate, size);
		log.info("findScoredBakeryBy ranksFromDb: {}", ranksFromDb.size());
		if (!ranksFromDb.isEmpty()) {
			return ranksFromDb;
		}

		scoredBakeryEventStream.publishCalculateRankingEvent(calculatedDate);

		final List<ScoredBakery> lastCalculatedRank = getLastCalculatedRanks(calculatedDate, size);
		log.info("findScoredBakeryBy getLastCalculatedRanks: {}", lastCalculatedRank.size());
		if (!lastCalculatedRank.isEmpty()) {
			return lastCalculatedRank;
		}

		throw new DaedongException(DaedongStatus.CALCULATING_BAKERY_RANKING);
	}

	private List<ScoredBakery> getLastCalculatedRanks(final LocalDate calculatedDate, final int size) {
		return scoredBakeryRepository.findScoredBakeryByCalculatedDate(calculatedDate.minusDays(1L), size);
	}

	private List<ScoredBakery> getRanksFromDb(final LocalDate calculatedDate, final int size) {
		return scoredBakeryRepository.findScoredBakeryByCalculatedDate(calculatedDate, size);
	}

	private List<FlagBakery> findFlagBakeryBy(final Long userId, final List<ScoredBakery> bakeriesScores) {
		return flagBakeryRepository.findByUserIdAndBakeryIdIn(
			userId,
			bakeriesScores.stream()
				.map(scoredBakery -> scoredBakery.getBakery().getId())
				.toList()
		);
	}

	private BakeryRankingCard from(final List<FlagBakery> flagBakeryList, final ScoredBakery bakeryScores) {
		return BakeryRankingCard.builder()
			.id(bakeryScores.getBakery().getId())
			.name(bakeryScores.getBakery().getName())
			.image(bakeryScores.getBakery().getImage())
			.shortAddress(bakeryScores.getBakery().getShortAddress())
			.isFlagged(isUserFlaggedBakery(bakeryScores, flagBakeryList))
			.calculatedDate(bakeryScores.getCalculatedDate())
			.build();
	}

	private boolean isUserFlaggedBakery(
		final ScoredBakery bakeryScores,
		final List<FlagBakery> flagBakeryList
	) {
		return flagBakeryList.stream()
			.anyMatch(flagBakery ->
				flagBakery.getBakery().getId()
					.equals(bakeryScores.getBakery().getId())
			);
	}
}
