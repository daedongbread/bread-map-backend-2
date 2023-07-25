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
	public int calculateBakeryScore(final List<BakeryScoreBaseWithSelectedDate> bakeryScoreBaseList) {
		final List<ScoredBakery> sortedBakeryRank =
			bakeryScoreBaseList
				.stream()
				.map(ScoredBakery::from)
				.sorted(
					Comparator.comparing(ScoredBakery::getTotalScore).reversed()
						.thenComparing(scoredBakery -> scoredBakery.getBakery().getId()).reversed()
				)
				.toList();

		for (final ScoredBakery scoredBakery : sortedBakeryRank) {
			scoredBakery.setRank(sortedBakeryRank.indexOf(scoredBakery) + 1);
		}

		return scoredBakeryRepository.bulkInsert(sortedBakeryRank);
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
		final List<ScoredBakery> ranksFromDb = getRanksFromDb(calculatedDate, size);
		if (!ranksFromDb.isEmpty()) {
			return ranksFromDb;
		}

		scoredBakeryEventStream.publishCalculateRankingEvent(calculatedDate);

		final List<ScoredBakery> lastCalculatedRank = getLastCalculatedRanks(calculatedDate, size);
		if (!lastCalculatedRank.isEmpty()) {
			return lastCalculatedRank;
		}
		return List.of();
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
