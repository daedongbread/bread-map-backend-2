package com.depromeet.breadmapbackend.domain.bakery;

import java.util.Map;

import org.springframework.data.redis.connection.stream.StreamInfo;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * BakeryViewEventStreamRedisImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/10
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class BakeryViewEventStreamRedisImpl implements BakeryViewEventStream {

	private static final String BAKERY_VIEW_EVENT = "bakery-view-event";
	private final StringRedisTemplate redisTemplate;

	@Override
	public void publish(final Map<String, String> fieldMap) {
		redisTemplate.opsForStream().add(BAKERY_VIEW_EVENT, fieldMap);
		try {
			final StreamInfo.XInfoConsumers consumers = redisTemplate.opsForStream()
				.consumers(BAKERY_VIEW_EVENT, "bakery-view-event:group");
			if (consumers.size() > 0) {
				redisTemplate.opsForStream().createGroup(BAKERY_VIEW_EVENT, "bakery-view-event:group");
			}
		} catch (Exception e) {
			log.info("bakery-view-event:group already exists : {} ",e.getMessage());
		}
	}
}
