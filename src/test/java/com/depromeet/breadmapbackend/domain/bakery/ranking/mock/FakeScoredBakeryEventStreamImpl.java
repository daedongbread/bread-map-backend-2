package com.depromeet.breadmapbackend.domain.bakery.ranking.mock;

import java.util.HashMap;

import com.depromeet.breadmapbackend.domain.bakery.ranking.ScoredBakeryEventStream;
import com.depromeet.breadmapbackend.domain.bakery.ranking.ScoredBakeryEvents;

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
	public void publish(final ScoredBakeryEvents event, final String yearWeekOfMonth) {
		fieldMap.put(event.name(), yearWeekOfMonth);
	}

}
