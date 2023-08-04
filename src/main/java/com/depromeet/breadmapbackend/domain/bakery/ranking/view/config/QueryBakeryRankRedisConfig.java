package com.depromeet.breadmapbackend.domain.bakery.ranking.view.config;

import static com.depromeet.breadmapbackend.global.EventConsumerGroupInfo.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.Subscription;

import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.BakeryRankView;
import com.depromeet.breadmapbackend.domain.bakery.ranking.view.interfaces.BakeryFlagCountChangeEventListener;
import com.depromeet.breadmapbackend.domain.bakery.ranking.view.interfaces.BakeryRankingCalculationDoneEventListener;
import com.depromeet.breadmapbackend.domain.bakery.ranking.view.interfaces.BakeryRatingChangeEventListener;
import com.depromeet.breadmapbackend.global.EventInfo;
import com.depromeet.breadmapbackend.global.utils.EventSubscriptionHelper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;

/**
 * QueryBakeryRankRedisConfig
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/02
 */

@Configuration
@RequiredArgsConstructor
public class QueryBakeryRankRedisConfig {

	@Value("${redis.poll-timeout.bakery-ranking-calculator}")
	private int bakeryRankingCalculatorPollTimeOut;
	private final BakeryRankingCalculationDoneEventListener bakeryRankingCalculationDoneEventListener;
	private final BakeryFlagCountChangeEventListener bakeryFlagCountChangeEventListener;
	private final BakeryRatingChangeEventListener bakeryRatingChangeEventListener;

	private final EventSubscriptionHelper eventSubscriptionHelper;

	@Bean
	public Subscription bakeryRankingCalculationDoneEventSubscription(RedisConnectionFactory factory) {
		final EventInfo bakeryRankingCalculationDoneEvent = EventInfo.BAKERY_RANKING_CALCULATION_DONE_EVENT;
		final String eventName = bakeryRankingCalculationDoneEvent.getEventName();
		final String consumerGroupName = bakeryRankingCalculationDoneEvent.getConsumerGroupName(BAKERY_RANKING_VIEW);

		eventSubscriptionHelper.registerConsumerGroup(eventName, consumerGroupName);

		return eventSubscriptionHelper.getSubscription(
			factory,
			eventName,
			consumerGroupName,
			bakeryRankingCalculationDoneEventListener,
			bakeryRankingCalculatorPollTimeOut
		);
	}

	@Bean
	public Subscription bakeryFlagCountChangeEventSubscription(RedisConnectionFactory factory) {
		final EventInfo bakeryFlagCountChangeEvent = EventInfo.BAKERY_FLAG_COUNT_CHANGE_EVENT;
		final String eventName = bakeryFlagCountChangeEvent.getEventName();
		final String consumerGroupName = bakeryFlagCountChangeEvent.getConsumerGroupName(BAKERY_RANKING_VIEW);

		eventSubscriptionHelper.registerConsumerGroup(eventName, consumerGroupName);

		return eventSubscriptionHelper.getSubscription(
			factory,
			eventName,
			consumerGroupName,
			bakeryFlagCountChangeEventListener,
			bakeryRankingCalculatorPollTimeOut
		);
	}

	@Bean
	public Subscription bakeryRatingChangeEventSubscription(RedisConnectionFactory factory) {
		final EventInfo bakeryLikeCountChangeEvent = EventInfo.BAKERY_RATING_CHANGE_EVENT;
		final String eventName = bakeryLikeCountChangeEvent.getEventName();
		final String consumerGroupName = bakeryLikeCountChangeEvent.getConsumerGroupName(BAKERY_RANKING_VIEW);

		eventSubscriptionHelper.registerConsumerGroup(eventName, consumerGroupName);

		return eventSubscriptionHelper.getSubscription(
			factory,
			eventName,
			consumerGroupName,
			bakeryRatingChangeEventListener,
			bakeryRankingCalculatorPollTimeOut
		);
	}

	@Bean
	public Subscription bakeryRankChangeEventSubscription(RedisConnectionFactory factory) {
		final EventInfo bakeryLikeCountChangeEvent = EventInfo.BAKERY_RANK_CHANGE_EVENT;
		final String eventName = bakeryLikeCountChangeEvent.getEventName();
		final String consumerGroupName = bakeryLikeCountChangeEvent.getConsumerGroupName(BAKERY_RANKING_VIEW);

		eventSubscriptionHelper.registerConsumerGroup(eventName, consumerGroupName);

		return eventSubscriptionHelper.getSubscription(
			factory,
			eventName,
			consumerGroupName,
			bakeryRatingChangeEventListener,
			bakeryRankingCalculatorPollTimeOut
		);
	}

	@Bean
	public RedisTemplate<String, BakeryRankView> userRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		final ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.registerModule(new JavaTimeModule())
			.disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);

		final Jackson2JsonRedisSerializer<BakeryRankView> serializer =
			new Jackson2JsonRedisSerializer<>(BakeryRankView.class);
		serializer.setObjectMapper(objectMapper);
		RedisTemplate<String, BakeryRankView> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(serializer);
		return redisTemplate;
	}

}
