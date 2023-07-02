package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.bakery.ranking.dto.BakeryScores;

/**
 * ScoredBakeryServiceImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
@Service
class ScoredBakeryServiceImpl implements ScoredBakeryService {

	private final ScoredBakeryRepository scoredBakeryRepository;

	ScoredBakeryServiceImpl(final ScoredBakeryRepository scoredBakeryRepository) {
		this.scoredBakeryRepository = scoredBakeryRepository;
	}

	@Transactional
	public int registerBakeriesRank(final List<BakeryScores> bakeriesScores) {
		final List<ScoredBakery> scoredBakeryList =
			bakeriesScores.stream()
				.map(ScoredBakery::from)
				.toList();

		return scoredBakeryRepository.bulkInsert(scoredBakeryList);
	}

	@Override
	public List<ScoredBakery> findBakeriesRankTop(final int count) {
		return scoredBakeryRepository.findBakeriesRankTop(count);
	}
}
