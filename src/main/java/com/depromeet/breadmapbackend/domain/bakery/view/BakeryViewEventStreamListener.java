package com.depromeet.breadmapbackend.domain.bakery.view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.global.EventInfo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BakeryViewEventStreamListener
	implements StreamListener<String, MapRecord<String, String, String>> {

	private final BakeryViewCounterService bakeryViewCounterService;

	@Override
	public void onMessage(final MapRecord<String, String, String> message) {
		final List<String> keys = EventInfo.BAKERY_VIEW_EVENT.getEvenMessageKeys();
		final Map<String, String> value = message.getValue();

		final Long bakeryId = Long.parseLong(value.get(keys.get(0)));
		final LocalDate viewDate = LocalDate.parse(value.get(keys.get(1)), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		bakeryViewCounterService.increaseBakeryViewCount(bakeryId, viewDate);
	}

}
