package com.depromeet.breadmapbackend.domain.flag;

/**
 * BakeryRankViewChangeEvent
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/04
 */
public interface BakeryRankViewFlagCountChangeEvent {
	void publish(final Long bakeryId);
}
