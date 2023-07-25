package com.depromeet.breadmapbackend.domain.bakery;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryScoreBaseWithSelectedDate;
import com.depromeet.breadmapbackend.domain.bakery.ranking.ScoredBakeryService;
import com.depromeet.breadmapbackend.global.EventInfo;
import com.depromeet.breadmapbackend.global.converter.LocalDateParser;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BakeryRankingCalculationEventStreamListener
	implements StreamListener<String, MapRecord<String, String, String>> {

	private final BakeryService bakeryService;
	private final ScoredBakeryService scoredBakeryService;
	private final StringRedisTemplate redisTemplate;
	private static final EventInfo event = EventInfo.CALCULATE_RANKING_EVENT;

	@Transactional
	@Override
	public void onMessage(final MapRecord<String, String, String> message) {
		final List<String> keys = event.getEvenMessageKeys();
		final Map<String, String> value = message.getValue();

		final String EVENT_KEY = event.getEventName() + ":" + value.get(keys.get(0));

		final LocalDate calculateDate = LocalDateParser.parse(value.get(keys.get(0)));

		if (isFirstInstanceToCalculateRanks(EVENT_KEY)) {
			calculateRankAndSave(calculateDate);
			// removeExpiredCacheData(EVENT_KEY);
		}
	}

	private void removeExpiredCacheData(
		final String eventKey
	) {
		redisTemplate.opsForValue().getAndDelete(eventKey);
	}

	private void calculateRankAndSave(final LocalDate calculateDate) {
		final List<BakeryScoreBaseWithSelectedDate> bakeriesScoreFactors =
			bakeryService.getBakeriesScoreFactors(calculateDate);
		scoredBakeryService.calculateBakeryScore(bakeriesScoreFactors);
	}

	private boolean isFirstInstanceToCalculateRanks(final String EVENT_KEY) {
		final Optional<Long> incrementedValue = Optional.ofNullable(
			redisTemplate.opsForValue().increment(EVENT_KEY)
		);
		return incrementedValue.isPresent() && incrementedValue.get() == 1L;
	}
}
