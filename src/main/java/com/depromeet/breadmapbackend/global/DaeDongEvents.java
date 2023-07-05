package com.depromeet.breadmapbackend.global;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DaeDongEvents
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/04
 */

@Getter
@AllArgsConstructor
public enum DaeDongEvents {

	BAKERY_ADD_EVENT("new-bakery-cache-service-group");

	private final String group;

}
