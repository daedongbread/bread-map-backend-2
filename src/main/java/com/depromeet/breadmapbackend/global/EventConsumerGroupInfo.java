package com.depromeet.breadmapbackend.global;

import java.util.List;

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

	BAKERY_VIEW_COUNT("count-group", List.of("bakeryId", "viewDate")),;

	final String groupName;
	final List<String> evenMessageKeys;
}
