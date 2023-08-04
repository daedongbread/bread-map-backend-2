package com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.usecase;

/**
 * BakeryRankViewFlagCountChangeUseCase
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/04
 */
public interface BakeryRankViewRatingCountChangeUseCase {
	void command(Long bakeryId);
}
