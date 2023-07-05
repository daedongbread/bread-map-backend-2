package com.depromeet.breadmapbackend.global.infra;

import java.time.Duration;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import com.depromeet.breadmapbackend.domain.bakery.BakeryAddEventStreamListener;
import com.depromeet.breadmapbackend.global.DaeDongEvents;

import lombok.RequiredArgsConstructor;

/**
 * RedisStreamConfig
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/04
 */
@Configuration
@RequiredArgsConstructor
public class RedisStreamConfig {

	private final BakeryAddEventStreamListener bakeryAddEventStreamListener;

	@Bean
	public Subscription bakeryAddSubscription(RedisConnectionFactory factory) {
		final DaeDongEvents bakeryAddEvent = DaeDongEvents.BAKERY_ADD_EVENT;

		final StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
			StreamMessageListenerContainer
				.StreamMessageListenerContainerOptions
				.builder()
				.pollTimeout(Duration.ofSeconds(10))
				.build();

		final StreamMessageListenerContainer<String, MapRecord<String, String, String>> listenerContainer =
			StreamMessageListenerContainer.create(factory, options);

		final org.springframework.data.redis.stream.Subscription subscription =
			listenerContainer.receiveAutoAck(
				Consumer.from(bakeryAddEvent.getGroup(), "instance:" + getInstanceId()),
				StreamOffset.create(bakeryAddEvent.name(), ReadOffset.lastConsumed()),
				bakeryAddEventStreamListener
			);

		listenerContainer.start();
		return subscription;

	}

	private String getInstanceId() {
		return UUID.randomUUID().toString().substring(0, 8);
	}
}
