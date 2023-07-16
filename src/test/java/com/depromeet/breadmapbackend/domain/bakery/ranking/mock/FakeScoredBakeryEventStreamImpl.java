package com.depromeet.breadmapbackend.domain.bakery.ranking.mock;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import com.depromeet.breadmapbackend.domain.bakery.ranking.ScoredBakeryEventStream;
import com.depromeet.breadmapbackend.global.EventInfo;

/**
 * FakeScoreBakeryEventStreamImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/03
 */
public class FakeScoredBakeryEventStreamImpl implements ScoredBakeryEventStream {

	private static final HashMap<String, String> fieldMap = new HashMap<>();

	public static HashMap<String, String> getFieldMap() {
		return fieldMap;
	}

	public static void clearData() {
		fieldMap.clear();
	}

	@Override
	public void publishCalculateRankingEvent(final LocalDate calculatedDate) {
		fieldMap.put(EventInfo.RE_CALCULATE_RANKING_EVENT.name(),
			calculatedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
	}

	@Override
	public void publishCachingRankingEvent(final LocalDate calculatedDate) {
		fieldMap.put(EventInfo.RE_CACHE_RANKING_EVENT.name(),
			calculatedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
	}
}
