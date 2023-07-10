package com.depromeet.breadmapbackend.domain.bakery.view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BakeryViewEventStreamListener implements StreamListener<String, MapRecord<String, String, String>> {

	private final BakeryViewRepository repository;
	private final StringRedisTemplate redisTemplate;
	public static final String KEY = "BAKERY-VIEW:";

	@Transactional
	@Override
	public void onMessage(final MapRecord<String, String, String> message) {
		System.out.println("=-=========================");
		final Map<String, String> value = message.getValue();
		final Long bakeryId = Long.parseLong(value.get("bakeryId"));
		final LocalDate viewDate = LocalDate.parse(value.get("viewDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		final Long increment = redisTemplate.opsForValue()
			.increment(KEY + bakeryId + ":" + viewDate);

		repository.save(BakeryView.builder()
			.bakeryId(bakeryId)
			.viewDate(viewDate)
			.viewCount(increment)
			.build()
		);
	}
}
