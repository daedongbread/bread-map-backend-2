package com.depromeet.breadmapbackend.domain.bakery;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.bakery.dto.AddedBakeryCardDto;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

/**
 * BakeryAddEventStreamListener
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/04
 */
@Component
@RequiredArgsConstructor
public class BakeryAddEventStreamListener implements StreamListener<String, MapRecord<String, String, String>> {

	private final BakeryRepository bakeryRepository;
	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;
	private static final int DISPLAY_SIZE = 10;
	private static final String KEY = "BAKERY:ADDED";

	@Transactional
	@Override
	public void onMessage(final MapRecord<String, String, String> message) {

		final Bakery bakery = bakeryRepository.findById(getBakeryIdFrom(message))
			.orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));

		makeCachedBakeryCorrectSize();

		cacheNewBakery(bakery);
	}

	private void cacheNewBakery(final Bakery bakery) {
		String serializedValue = null;
		try {
			serializedValue = objectMapper.writeValueAsString(new AddedBakeryCardDto(bakery));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		redisTemplate.opsForZSet().add(
			KEY,
			serializedValue,
			TimeUnit.DAYS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
		);
	}

	private void makeCachedBakeryCorrectSize() {
		final Set<String> cachedBakery = redisTemplate.opsForZSet().range(KEY, 0, -1);

		if (cachedBakery != null && cachedBakery.size() >= DISPLAY_SIZE) {
			redisTemplate.opsForZSet().removeRange(KEY, 0, cachedBakery.size() - DISPLAY_SIZE);
		}
	}

	private Long getBakeryIdFrom(final MapRecord<String, String, String> message) {
		final Map<String, String> value = message.getValue();
		return Long.parseLong(value.get("bakeryId"));
	}

}
