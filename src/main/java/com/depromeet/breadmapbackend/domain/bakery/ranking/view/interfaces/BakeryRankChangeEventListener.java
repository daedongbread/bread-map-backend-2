package com.depromeet.breadmapbackend.domain.bakery.ranking.view.interfaces;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.usecase.BakeryRankViewRankChangeUseCase;
import com.depromeet.breadmapbackend.global.EventInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

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
public class BakeryRankChangeEventListener implements
	StreamListener<String, MapRecord<String, String, String>> {

	private final ObjectMapper objectMapper;
	private final BakeryRankViewRankChangeUseCase bakeryRankViewRankChangeUseCase;
	private static final EventInfo event = EventInfo.BAKERY_FLAG_COUNT_CHANGE_EVENT;

	@Override
	public void onMessage(final MapRecord<String, String, String> message) {

		bakeryRankViewRankChangeUseCase.command();

	}
}
