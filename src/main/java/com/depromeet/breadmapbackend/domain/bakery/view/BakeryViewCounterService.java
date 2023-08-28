package com.depromeet.breadmapbackend.domain.bakery.view;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * BakeryViewCounterService
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/28
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BakeryViewCounterService {

	private final BakeryViewRepository repository;
	private final StringRedisTemplate redisTemplate;
	private static final Long INITIAL_COUNTED_VALUE = 1L;
	private static final Long INITIAL_VALUE = 0L;

	public void increaseBakeryViewCount(final Long bakeryId, final LocalDate viewDate) {
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
			return redisTemplate.opsForValue()
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
				Duration.ofSeconds(getRankValueTTLInHours())
			);
			return incrementedViewCount;
		} else {
			redisTemplate.opsForValue()
				.set(
					getRedisViewCountKey(bakeryId, viewDate),
					INITIAL_COUNTED_VALUE.toString(),
					Duration.ofSeconds(getRankValueTTLInHours())
				);
			return INITIAL_COUNTED_VALUE;
		}
	}

	private String getRedisViewCountKey(final Long bakeryId, final LocalDate viewDate) {
		return "BAKERY-VIEW:" + bakeryId + ":" + viewDate;
	}

	private long getRankValueTTLInHours() {

		final int now = LocalTime.now().toSecondOfDay();
		final int endOfDateTime = LocalTime.MAX.toSecondOfDay() + (1 * 60 * 60);

		return (long)endOfDateTime - now;
	}

}
