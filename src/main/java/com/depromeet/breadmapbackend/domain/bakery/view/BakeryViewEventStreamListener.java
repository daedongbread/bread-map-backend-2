package com.depromeet.breadmapbackend.domain.bakery.view;

import static com.depromeet.breadmapbackend.global.EventConsumerGroupInfo.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BakeryViewEventStreamListener implements StreamListener<String, MapRecord<String, String, String>> {

	private final BakeryViewRepository repository;
	private final StringRedisTemplate redisTemplate;
	private static final Long INITIAL_COUNTED_VALUE = 1L;
	private static final Long INITIAL_VALUE = 0L;

	@Transactional
	@Override
	public void onMessage(final MapRecord<String, String, String> message) {
		final List<String> keys = BAKERY_VIEW_COUNT.getEvenMessageKeys();
		final Map<String, String> value = message.getValue();

		final Long bakeryId = Long.parseLong(value.get(keys.get(0)));
		final LocalDate viewDate = LocalDate.parse(value.get(keys.get(1)), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		final Long viewCount = getIncrementedViewCount(bakeryId, viewDate);

		repository.save(BakeryView.builder()
			.bakeryId(bakeryId)
			.viewDate(viewDate)
			.viewCount(viewCount)
			.build()
		);
	}

	private Long getIncrementedViewCount(final Long bakeryId, final LocalDate viewDate) {
		final Long cachedViewCount = Long.parseLong(
			Optional.ofNullable(
				redisTemplate.opsForValue()
					.get(getRedisViewCountKey(bakeryId, viewDate))
			).orElse("0")
		);
		if (Objects.equals(cachedViewCount, INITIAL_VALUE)) {
			return reCountWhenErrorOrInitialCount(bakeryId, viewDate);
		} else {
			return 	redisTemplate.opsForValue()
				.increment(getRedisViewCountKey(bakeryId, viewDate));
		}
	}

	private Long reCountWhenErrorOrInitialCount(final Long bakeryId, final LocalDate viewDate) {
		final Optional<BakeryView> bakeryView = repository.findById(new BakeryViewId(bakeryId, viewDate));

		if (bakeryView.isPresent()) {
			final Long incrementedViewCount = (bakeryView.get().getViewCount()) + 1L;
			redisTemplate.opsForValue().set(
				getRedisViewCountKey(bakeryId, viewDate),
				incrementedViewCount.toString(),
				Duration.ofHours(getRankValueTTLInHours())
			);
			return incrementedViewCount;
		} else {
			redisTemplate.opsForValue()
				.set(
					getRedisViewCountKey(bakeryId, viewDate),
					INITIAL_COUNTED_VALUE.toString(),
					Duration.ofHours(getRankValueTTLInHours())
				);
			return INITIAL_COUNTED_VALUE;
		}
	}

	private String getRedisViewCountKey(final Long bakeryId, final LocalDate viewDate) {
		return "BAKERY-VIEW:" + bakeryId + ":" + viewDate;
	}


	private Long getRankValueTTLInHours() {
		final LocalTime now = LocalTime.now();
		final LocalTime endOfDateTime = LocalTime.of(23, 59, 59);
		final LocalTime ttl = endOfDateTime.minusHours(now.getHour())
			.plusHours(2L);
		return Long.valueOf(ttl.getHour());
	}
}
