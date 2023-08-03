package com.depromeet.breadmapbackend.domain.bakery.ranking.command.interfaces;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.bakery.ranking.command.domain.usecase.impl.BakeryRankingCalculationUseCaseImpl;
import com.depromeet.breadmapbackend.global.EventInfo;
import com.depromeet.breadmapbackend.global.converter.LocalDateParser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BakeryRankingCalculationEventStreamListener
	implements StreamListener<String, MapRecord<String, String, String>> {

	private final BakeryRankingCalculationUseCaseImpl bakeryRankingCalculationUseCaseImpl;
	private static final EventInfo event = EventInfo.CALCULATE_BAKERY_RANKING_EVENT;

	@Override
	public void onMessage(final MapRecord<String, String, String> message) {
		log.info("=============== Event Stream Listener calculate ranking ===============");
		final List<String> keys = event.getEvenMessageKeys();
		final Map<String, String> value = message.getValue();

		final String EVENT_KEY = event.getEventName() + ":" + value.get(keys.get(0));

		final LocalDate calculateDate = LocalDateParser.parse(value.get(keys.get(0)));
		log.info("EVENT_KEY: {}", EVENT_KEY);
		log.info("checking if this instance is first instance to calculate ranking");
		bakeryRankingCalculationUseCaseImpl.command(EVENT_KEY, calculateDate);
	}

}
