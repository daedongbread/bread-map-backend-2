package com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain;

import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * RankWeight
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */

@Getter
@AllArgsConstructor
public enum RankWeight {
	RATING_WEIGHT(1),
	FLAG_COUNT_WEIGHT(1),
	VIEW_COUNT_WEIGHT(1);

	final double weight;
}
