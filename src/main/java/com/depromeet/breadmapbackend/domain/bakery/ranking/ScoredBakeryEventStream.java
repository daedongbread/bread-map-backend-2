package com.depromeet.breadmapbackend.domain.bakery.ranking;

import java.time.LocalDate;

import com.depromeet.breadmapbackend.global.EventInfo;

/**
 * ScoredBakeryEventStream
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/03
 */
public interface ScoredBakeryEventStream {

	void publish(final EventInfo event, final LocalDate calculatedDate);
}
