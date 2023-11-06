package com.depromeet.breadmapbackend.global.infra;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile({"default", "local"})
@Configuration
@RequiredArgsConstructor
public class EmbeddedRedisConfig {

	private static final String REDIS_DOCKER_IMAGE = "redis:6";
	private final StringRedisTemplate redisTemplate;

	static {
		GenericContainer<?> REDIS_CONTAINER =
			new GenericContainer<>(DockerImageName.parse(REDIS_DOCKER_IMAGE))
				.waitingFor( Wait.forLogMessage(".*Ready to accept connections.*\\n", 1))
				.withExposedPorts(6379)
				.withReuse(true);

		REDIS_CONTAINER.start();

		System.setProperty("spring.redis.host", REDIS_CONTAINER.getHost());
		System.setProperty("spring.redis.port", REDIS_CONTAINER.getMappedPort(6379).toString());


	}

	@PostConstruct
	public void setUp(){
		try {
			redisTemplate.opsForStream()
				.createGroup("bakery-view-event", "bakery-view-event:group");
		} catch (Exception e) {
			log.info("bakery-view-event:group already exists : {} ",e.getMessage());
		}
	}
}
