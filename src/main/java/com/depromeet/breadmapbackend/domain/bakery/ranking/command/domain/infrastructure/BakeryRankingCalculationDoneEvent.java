package com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.infrastructure;

import java.time.LocalDate;

/**
 * BakeryRankingCalculationDoneEvent
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/03
 */
public interface BakeryRankingCalculationDoneEvent {
	void publish(final LocalDate calculatedDate);
}
