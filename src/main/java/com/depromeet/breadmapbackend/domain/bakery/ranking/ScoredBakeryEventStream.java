package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.time.LocalDate;

/**
 * ScoredBakeryEventStream
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/03
 */
public interface ScoredBakeryEventStream {

	void publishCalculateRankingEvent(final LocalDate calculatedDate);

	void publishCachingRankingEvent(final LocalDate calculatedDate);
}
