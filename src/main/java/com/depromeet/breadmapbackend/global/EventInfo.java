package com.depromeet.breadmapbackend.global;

import java.util.List;

import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

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
public enum EventInfo {

	BAKERY_VIEW_EVENT("bakery-view-event", List.of(EventConsumerGroupInfo.BAKERY_VIEW_COUNT)),
	CALCULATE_RANKING_EVENT("calculate_raking_event",List.of()),
	CACHE_RANKING_EVENT("cache_ranking_event", List.of()),
	;

	final String eventName;
	final List<EventConsumerGroupInfo> consumerGroupInfos;

	public String getConsumerGroupName(EventConsumerGroupInfo consumerGroup) {
		return this.eventName + ":" + this.consumerGroupInfos.stream()
			.filter(consumerGroupInfo -> consumerGroupInfo.equals(consumerGroup))
			.findFirst()
			.orElseThrow(() -> new DaedongException(DaedongStatus.EVENT_DOES_NOT_HAVE_CONSUMER_GROUP))
			.getGroupName();
	}
}
