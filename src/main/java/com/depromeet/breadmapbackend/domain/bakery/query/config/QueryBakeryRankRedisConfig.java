package com.depromeet.breadmapbackend.domain.bakery.query.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.depromeet.breadmapbackend.domain.bakery.query.domain.QueryBakeryRank;
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

	@Bean
	public RedisTemplate<String, QueryBakeryRank> userRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		final ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.registerModule(new JavaTimeModule())
			.disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);

		final Jackson2JsonRedisSerializer<QueryBakeryRank> serializer =
			new Jackson2JsonRedisSerializer<>(QueryBakeryRank.class);
		serializer.setObjectMapper(objectMapper);
		RedisTemplate<String, QueryBakeryRank> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(serializer);
		return redisTemplate;
	}

}
