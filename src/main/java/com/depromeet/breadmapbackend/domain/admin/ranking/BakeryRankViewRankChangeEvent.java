package com.depromeet.breadmapbackend.domain.admin.ranking;

import com.depromeet.breadmapbackend.global.EventInfo;

/**
 * BakeryRankViewChangeEvent
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/04
 */
public interface BakeryRankViewRankChangeEvent {
	void publish(EventInfo eventInfo, String value);
}
