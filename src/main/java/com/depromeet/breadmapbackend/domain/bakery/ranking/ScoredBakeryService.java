package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.util.List;

import com.depromeet.breadmapbackend.domain.bakery.ranking.dto.BakeryRankingCard;
import com.depromeet.breadmapbackend.domain.bakery.ranking.dto.BakeryScores;

/**
 * ScoredBakeryService
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
public interface ScoredBakeryService {
	int registerBakeriesRank(final List<BakeryScores> bakeriesScores, final String weekOfMonthYear);

	List<BakeryRankingCard> findBakeriesRankTop(final Long userId, final int count);
}
