package com.depromeet.breadmapbackend.domain.bakery.ranking.command.config;

import static com.depromeet.breadmapbackend.global.EventConsumerGroupInfo.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.stream.Subscription;

import com.depromeet.breadmapbackend.domain.bakery.ranking.command.interfaces.BakeryRankingCalculationEventStreamListener;
import com.depromeet.breadmapbackend.global.EventInfo;
import com.depromeet.breadmapbackend.global.utils.EventSubscriptionHelper;

import lombok.RequiredArgsConstructor;

/**
 * ScoredBakeryRedisConfig
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/04
 */
@Configuration
@RequiredArgsConstructor
public class ScoredBakeryRedisConfig {

	@Value("${redis.poll-timeout.bakery-ranking-calculator}")
	private int bakeryRankingCalculatorPollTimeOut;
	private final BakeryRankingCalculationEventStreamListener bakeryRankingCalculationEventStreamListener;
	private final EventSubscriptionHelper eventSubscriptionHelper;

	@Bean
	public Subscription bakeryRankingCalculationSubscription(RedisConnectionFactory factory) {
		final EventInfo calculateRankingEvent = EventInfo.CALCULATE_BAKERY_RANKING_EVENT;
		final String eventName = calculateRankingEvent.getEventName();
		final String consumerGroupName = calculateRankingEvent.getConsumerGroupName(CALCULATE_RANKING);

		eventSubscriptionHelper.registerConsumerGroup(eventName, consumerGroupName);

		return eventSubscriptionHelper.getSubscription(
			factory,
			eventName,
			consumerGroupName,
			bakeryRankingCalculationEventStreamListener,
			bakeryRankingCalculatorPollTimeOut
		);
	}

}
