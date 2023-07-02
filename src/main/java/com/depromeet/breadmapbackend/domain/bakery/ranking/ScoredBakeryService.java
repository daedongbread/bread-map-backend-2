package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.util.List;

import com.depromeet.breadmapbackend.domain.bakery.ranking.dto.BakeryScores;

/**
 * ScoredBakeryService
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
interface ScoredBakeryService {
	int registerBakeriesRank(List<BakeryScores> bakeriesScores);

	List<ScoredBakery> findBakeriesRankTop(int count);
}
