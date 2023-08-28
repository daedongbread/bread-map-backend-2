package com.depromeet.breadmapbackend.global;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * EventInformation
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/13
 */
@AllArgsConstructor
@Getter
public enum EventConsumerGroupInfo {

	BAKERY_VIEW_COUNT("count-group"),
	CALCULATE_RANKING("calculate-group"),
	CACHE_RANKING("cache-group"),
	;
	final String groupName;

}
