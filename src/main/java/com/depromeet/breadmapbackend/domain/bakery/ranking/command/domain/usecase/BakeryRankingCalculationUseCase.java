package com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.usecase;

import java.time.LocalDate;

/**
 * ScheduledBakeryRankCalculationUseCase
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/03
 */
public interface BakeryRankingCalculationUseCase {

	void command(final String EVENT_KEY, final LocalDate calculateDate);
}
