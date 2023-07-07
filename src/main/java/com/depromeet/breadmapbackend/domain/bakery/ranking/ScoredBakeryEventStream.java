package com.depromeet.breadmapbackend.domain.bakery.ranking;

/**
 * ScoredBakeryEventStream
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/03
 */
public interface ScoredBakeryEventStream {

	void publish(final ScoredBakeryEvents event, final String yearWeekOfMonth);
}
