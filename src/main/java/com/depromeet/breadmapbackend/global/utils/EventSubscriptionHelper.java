package com.depromeet.breadmapbackend.global.utils;

import java.time.Duration;
import java.util.UUID;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * EventSubscriptionHelper
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/04
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class EventSubscriptionHelper {

	private static final String INSTANCE = "instance";
	private final StringRedisTemplate redisTemplate;

	public void registerConsumerGroup(final String eventName, final String consumerGroupName) {
		try {
			redisTemplate.opsForStream().consumers(eventName, consumerGroupName);
			log.info(consumerGroupName + " already exists");
		} catch (Exception e) {
			redisTemplate.opsForStream().createGroup(eventName, consumerGroupName);
		}
	}

	public Subscription getSubscription(
		final RedisConnectionFactory factory,
		final String eventName,
		final String consumerGroupName,
		final StreamListener<String, MapRecord<String, String, String>> streamListener,
		final int pollTimeoutSeconds
	) {
		final StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
			StreamMessageListenerContainer
				.StreamMessageListenerContainerOptions
				.builder()
				.pollTimeout(Duration.ofSeconds(pollTimeoutSeconds))
				.build();

		final StreamMessageListenerContainer<String, MapRecord<String, String, String>> listenerContainer =
			StreamMessageListenerContainer.create(factory, options);

		final Subscription subscription =
			listenerContainer.receiveAutoAck(
				Consumer.from(consumerGroupName, INSTANCE + ":" + getInstanceId()),
				StreamOffset.create(eventName, ReadOffset.lastConsumed()),
				streamListener
			);

		listenerContainer.start();
		return subscription;
	}

	private String getInstanceId() {
		return UUID.randomUUID().toString().substring(0, 8);
	}
}
