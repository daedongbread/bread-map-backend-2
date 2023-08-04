package com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.usecase;

import java.util.List;

/**
 * BakeryRankViewRankChangeUseCase
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/04
 */
public interface BakeryRankViewRankChangeUseCase {
	void command(final List<Command> command);

	record Command(
		Long bakeryId,
		int rank
	) {
	}

}
