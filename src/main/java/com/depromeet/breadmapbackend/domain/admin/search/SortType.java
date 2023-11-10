package com.depromeet.breadmapbackend.domain.admin.search;

import java.util.Arrays;

import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

/**
 * SearchType
 *
 * @author jaypark
 * @version 1.0.0
 * @since 11/10/23
 */
public enum SortType {
	ONE_WEEK,
	ONE_MONTH,
	THREE_MONTH,
	;

	public static SortType find(final String sortType) {
		return Arrays.stream(SortType.values())
			.filter(type -> type.name().equals(sortType))
			.findFirst()
			.orElseThrow(() -> new DaedongException(DaedongStatus.NOT_EXISTS_SORT_TYPE));
	}
}
