package com.depromeet.breadmapbackend.domain.bakery;

import java.util.Map;
import java.util.Objects;

import org.springframework.data.redis.connection.stream.StreamInfo;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.global.EventInfo;

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

	private final StringRedisTemplate redisTemplate;

	@Override
	public void publish(final Map<String, String> fieldMap) {
		redisTemplate.opsForStream().add(EventInfo.BAKERY_VIEW_EVENT.getEventName(), fieldMap);
	}
}
