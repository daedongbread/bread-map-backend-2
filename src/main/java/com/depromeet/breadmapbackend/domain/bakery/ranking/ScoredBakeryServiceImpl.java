package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryRankingCard;
import com.depromeet.breadmapbackend.domain.bakery.ranking.dto.BakeryScores;
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

@RequiredArgsConstructor
@Service
public class ScoredBakeryServiceImpl implements ScoredBakeryService {

	private final ScoredBakeryRepository scoredBakeryRepository;
	private final FlagBakeryRepository flagBakeryRepository;

	@Transactional
	public int registerBakeriesRank(final List<BakeryScores> bakeriesScores) {
		final List<ScoredBakery> scoredBakeryList =
			bakeriesScores.stream()
				.map(ScoredBakery::from)
				.toList();

		return scoredBakeryRepository.bulkInsert(scoredBakeryList);
	}

	@Override
	public List<BakeryRankingCard> findBakeriesRankTop(final Long userId, final int size) {
		final List<ScoredBakery> bakeriesScores =
			scoredBakeryRepository.findBakeriesRankTop(size); // TODO : redis caching 적용

		final List<FlagBakery> flagBakeryList = findUserFlagBakeries(userId, bakeriesScores);

		return bakeriesScores.stream()
			.map(bakeryScores ->
				BakeryRankingCard.builder()
					.id(bakeryScores.getBakery().getId())
					.name(bakeryScores.getBakery().getName())
					.image(bakeryScores.getBakery().getImage())
					.flagNum(bakeryScores.getFlagCount())
					.rating(bakeryScores.getBakeryRating())
					.shortAddress(bakeryScores.getBakery().getShortAddress())
					.isFlagged(doesUserFlaggedBakery(bakeryScores, flagBakeryList))
					.build()
			)
			.limit(size)
			.toList();

	}

	private List<FlagBakery> findUserFlagBakeries(final Long userId, final List<ScoredBakery> bakeriesScores) {
		return flagBakeryRepository.findByUserIdAndBakeryIdIn(
			userId,
			bakeriesScores.stream()
				.map(scoredBakery -> scoredBakery.getBakery().getId())
				.toList()
		);
	}

	private boolean doesUserFlaggedBakery(
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
