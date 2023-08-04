package com.depromeet.breadmapbackend.domain.bakery.ranking.view.interfaces;

import java.util.List;
import java.util.Map;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.usecase.BakeryRankViewFlagCountChangeUseCase;
import com.depromeet.breadmapbackend.global.EventInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * BakeryFlagCountChangeEventListener
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BakeryFlagCountChangeEventListener implements
	StreamListener<String, MapRecord<String, String, String>> {

	private final BakeryRankViewFlagCountChangeUseCase bakeryRankViewFlagCountChangeUseCase;
	private static final EventInfo event = EventInfo.BAKERY_FLAG_COUNT_CHANGE_EVENT;

	@Override
	public void onMessage(final MapRecord<String, String, String> message) {
		final List<String> keys = event.getEvenMessageKeys();
		final Map<String, String> value = message.getValue();
		final Long bakeryId = Long.parseLong(value.get(keys.get(0)));

		bakeryRankViewFlagCountChangeUseCase.command(bakeryId);
	}
}
