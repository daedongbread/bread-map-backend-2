package com.depromeet.breadmapbackend.global.infra;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import com.depromeet.breadmapbackend.global.infra.properties.CustomRedisProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import redis.embedded.RedisServer;

@Slf4j
@Profile({"default", "local" }) // TODO
@Configuration
@RequiredArgsConstructor
public class EmbeddedRedisConfig {
	private final CustomRedisProperties customRedisProperties;

	private RedisServer redisServer;

	@PostConstruct
	public void redisServer() {
		log.info("Connect to Embedded-Redis");
		redisServer = new RedisServer(customRedisProperties.getPort());
		redisServer.start();
	}

	@PreDestroy
	public void stopRedis() {
		if (redisServer != null && redisServer.isActive()) {
			redisServer.stop();
		}
	}

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		log.info("Connect to Redis");
		return new LettuceConnectionFactory(
			customRedisProperties.getHost(),
			customRedisProperties.getPort()
		);  // Lettuce 사용
	}
}
