package com.depromeet.breadmapbackend.global.infra;

import static com.depromeet.breadmapbackend.global.EventConsumerGroupInfo.*;

import java.time.Duration;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import com.depromeet.breadmapbackend.domain.bakery.BakeryRankingCalculationEventStreamListener;
import com.depromeet.breadmapbackend.domain.bakery.view.BakeryViewEventStreamListener;
import com.depromeet.breadmapbackend.global.EventInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RedisStreamConfig
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/10
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisStreamConfig {

	private final BakeryViewEventStreamListener bakeryViewEventStreamListener;
	private final BakeryRankingCalculationEventStreamListener bakeryRankingCalculationEventStreamListener;
	private final StringRedisTemplate redisTemplate;
	private static final String INSTANCE = "instance";

	@Bean
	public Subscription bakeryViewSubscription(RedisConnectionFactory factory) {
		final EventInfo bakeryViewEvent = EventInfo.BAKERY_VIEW_EVENT;
		final String eventName = bakeryViewEvent.getEventName();
		final String consumerGroupName = bakeryViewEvent.getConsumerGroupName(BAKERY_VIEW_COUNT);

		registerConsumerGroup(eventName, consumerGroupName);

		return getSubscription(factory, eventName, consumerGroupName, bakeryViewEventStreamListener, 10);
	}

	@Bean
	public Subscription bakeryRankingCalculationSubscription(RedisConnectionFactory factory) {
		final EventInfo calculateRankingEvent = EventInfo.CALCULATE_RANKING_EVENT;
		final String eventName = calculateRankingEvent.getEventName();
		final String consumerGroupName = calculateRankingEvent.getConsumerGroupName(CALCULATE_RANKING);

		registerConsumerGroup(eventName, consumerGroupName);

		return getSubscription(factory, eventName, consumerGroupName, bakeryRankingCalculationEventStreamListener, 60);
	}

	private void registerConsumerGroup(final String eventName, final String consumerGroupName) {
		try {
			redisTemplate.opsForStream().consumers(eventName, consumerGroupName);
			log.info(consumerGroupName + " already exists");
		} catch (Exception e) {
			redisTemplate.opsForStream().createGroup(eventName, consumerGroupName);
		}
	}

	private Subscription getSubscription(
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
