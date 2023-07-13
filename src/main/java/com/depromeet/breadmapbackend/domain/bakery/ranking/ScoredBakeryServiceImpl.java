package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.bakery.ranking.dto.BakeryRankingCard;
import com.depromeet.breadmapbackend.domain.bakery.ranking.dto.BakeryScores;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
import com.depromeet.breadmapbackend.domain.flag.FlagBakeryRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.global.util.CalenderUtil;

import lombok.RequiredArgsConstructor;

/**
 * ScoredBakeryServiceImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ScoredBakeryServiceImpl implements ScoredBakeryService {

	private final ScoredBakeryRepository scoredBakeryRepository;
	private final FlagBakeryRepository flagBakeryRepository;
	private final ScoredBakeryEventStream scoredBakeryEventStream;

	@Transactional
	public int registerBakeriesRank(final List<BakeryScores> bakeriesScores, final String weekOfMonthYear) {
		final List<ScoredBakery> scoredBakeryList =
			bakeriesScores.stream()
				.map(ScoredBakery::from)
				.toList();

		return scoredBakeryRepository.bulkInsert(scoredBakeryList, weekOfMonthYear);
	}

	@Override
	public List<BakeryRankingCard> findBakeriesRankTop(final Long userId, final int size) {
		final List<ScoredBakery> scoredBakeries =
			findScoredBakeryBy(CalenderUtil.getYearWeekOfMonth(LocalDate.now()), size);

		final List<FlagBakery> userFlaggedBakeries = findFlagBakeryBy(userId, scoredBakeries);

		return scoredBakeries.stream()
			.map(bakeryScores -> from(userFlaggedBakeries, bakeryScores))
			.limit(size)
			.toList();

	}

	private List<ScoredBakery> findScoredBakeryBy(final String weekOfMonthYear, final int size) {

		final List<ScoredBakery> cachedRank =
			scoredBakeryRepository.findCachedScoredBakeryByWeekOfMonthYear(weekOfMonthYear, size);
		if (!cachedRank.isEmpty()) {
			return cachedRank;
		}

		final List<ScoredBakery> rankedBakeries =
			scoredBakeryRepository.findScoredBakeryByWeekOfMonthYear(weekOfMonthYear, size);
		if (!rankedBakeries.isEmpty()) {
			scoredBakeryEventStream.publish(ScoredBakeryEvents.CACHE_RANKING, weekOfMonthYear);
			return rankedBakeries;
		}

		scoredBakeryEventStream.publish(ScoredBakeryEvents.CALCULATE_RANKING, weekOfMonthYear);
		throw new DaedongException(DaedongStatus.CALCULATING_BAKERY_RANKING);
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
			.flagNum(bakeryScores.getFlagCount())
			.rating(bakeryScores.getBakeryRating())
			.shortAddress(bakeryScores.getBakery().getShortAddress())
			.isFlagged(isUserFlaggedBakery(bakeryScores, flagBakeryList))
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
