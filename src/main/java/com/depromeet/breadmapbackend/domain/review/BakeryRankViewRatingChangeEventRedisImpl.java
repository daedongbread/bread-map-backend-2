package com.depromeet.breadmapbackend.domain.review;

import java.util.HashMap;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.global.EventInfo;

import lombok.RequiredArgsConstructor;

/**
 * BakeryRankViewChangeEventImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/04
 */
@Component
@RequiredArgsConstructor
public class BakeryRankViewRatingChangeEventRedisImpl implements BakeryRankViewRatingChangeEvent {

	private static final EventInfo EVENT = EventInfo.BAKERY_RANK_CHANGE_EVENT;
	private final StringRedisTemplate redisTemplate;

	@Override
	public void publish(final Long bakeryId) {
		final HashMap<String, String> fieldMap = new HashMap<>();
		fieldMap.put(EVENT.getEvenMessageKeys().get(0), String.valueOf(bakeryId));
		redisTemplate.opsForStream().add(EVENT.getEventName(), fieldMap);
	}
}
