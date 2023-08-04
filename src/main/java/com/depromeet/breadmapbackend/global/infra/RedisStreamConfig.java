package com.depromeet.breadmapbackend.global.infra;

import static com.depromeet.breadmapbackend.global.EventConsumerGroupInfo.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.stream.Subscription;

import com.depromeet.breadmapbackend.domain.bakery.view.BakeryViewEventStreamListener;
import com.depromeet.breadmapbackend.global.EventInfo;
import com.depromeet.breadmapbackend.global.utils.EventSubscriptionHelper;

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

	@Value("${redis.poll-timeout.bakery-view}")
	private int bakeryViewPollTimeOut;

	private final BakeryViewEventStreamListener bakeryViewEventStreamListener;
	private final EventSubscriptionHelper eventSubscriptionHelper;

	@Bean
	public Subscription bakeryViewSubscription(RedisConnectionFactory factory) {
		final EventInfo bakeryViewEvent = EventInfo.BAKERY_VIEW_EVENT;
		final String eventName = bakeryViewEvent.getEventName();
		final String consumerGroupName = bakeryViewEvent.getConsumerGroupName(BAKERY_VIEW_COUNT);

		eventSubscriptionHelper.registerConsumerGroup(eventName, consumerGroupName);

		return eventSubscriptionHelper.getSubscription(
			factory,
			eventName,
			consumerGroupName,
			bakeryViewEventStreamListener,
			bakeryViewPollTimeOut
		);
	}

}
