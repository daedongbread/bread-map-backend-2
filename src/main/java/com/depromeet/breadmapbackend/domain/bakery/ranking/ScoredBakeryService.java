package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.util.List;

import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryScoreBase;
import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryScoreBaseWithSelectedDate;
import com.depromeet.breadmapbackend.domain.bakery.ranking.dto.BakeryRankingCard;

/**
 * ScoredBakeryService
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */
public interface ScoredBakeryService {
	int calculateBakeryScore(final List<BakeryScoreBaseWithSelectedDate> bakeryScoreBaseList);

	List<BakeryRankingCard> findBakeriesRankTop(final Long userId, final int count);
}
