package com.depromeet.breadmapbackend.domain.review;

/**
 * BakeryRankViewChangeEvent
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/04
 */
public interface BakeryRankViewRatingChangeEvent {
	void publish(final Long bakeryId);
}
