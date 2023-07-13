package com.depromeet.breadmapbackend.domain.bakery.view;

import static com.depromeet.breadmapbackend.global.EventConsumerGroupInfo.*;

import java.time.LocalDate;
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
	private static final Long INITIAL_COUNT = 1L;

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
		final Long cachedViewCount = redisTemplate.opsForValue()
			.increment(getRedisViewCountKey(bakeryId, viewDate));

		if (cachedViewCount == null || Objects.equals(cachedViewCount , INITIAL_COUNT)) {
			return reCountWhenErrorOrInitialCount(bakeryId, viewDate);
		}else {
			return cachedViewCount;
		}
	}

	private Long reCountWhenErrorOrInitialCount(final Long bakeryId, final LocalDate viewDate) {
		final Optional<BakeryView> bakeryView = repository.findById(new BakeryViewId(bakeryId, viewDate));

		if (bakeryView.isPresent()) {
			final Long incrementedViewCount = (bakeryView.get().getViewCount()) + 1L;
			redisTemplate.opsForValue().set(getRedisViewCountKey(bakeryId, viewDate), incrementedViewCount.toString());
			return incrementedViewCount;
		}else{
			return INITIAL_COUNT;
		}
	}

	private String getRedisViewCountKey(final Long bakeryId, final LocalDate viewDate) {
		return "BAKERY-VIEW:" + bakeryId + ":" + viewDate;
	}
}
