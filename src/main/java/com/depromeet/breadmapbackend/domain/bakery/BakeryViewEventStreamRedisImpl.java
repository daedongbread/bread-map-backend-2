package com.depromeet.breadmapbackend.domain.bakery;

import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * BakeryViewEventStreamRedisImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/10
 */

@Component
@RequiredArgsConstructor
public class BakeryViewEventStreamRedisImpl implements BakeryViewEventStream {

	private static final String BAKERY_VIEW_EVENT = "bakery-view-event";
	private final StringRedisTemplate redisTemplate;

	@Override
	public void publish(final Map<String, String> fieldMap) {
		redisTemplate.opsForStream().add(BAKERY_VIEW_EVENT, fieldMap);
		try{

			redisTemplate.opsForStream().createGroup(BAKERY_VIEW_EVENT, "bakery-view-event:group");
		}catch (Exception e){
			System.out.println(e.getMessage());
		}
	}
}
