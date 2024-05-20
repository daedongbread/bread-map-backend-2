package com.depromeet.breadmapbackend.global.infra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

@Slf4j
@Profile({"default", "local"})
@Configuration
@RequiredArgsConstructor
public class LocalMysqlConfig {

	private static final String MYSQL_DOCKER_IMAGE = "mysql:8.0.36";

	static {
		GenericContainer<?> MYSQL_CONTAINER =
			new GenericContainer<>(DockerImageName.parse(MYSQL_DOCKER_IMAGE))
				.waitingFor(Wait.forLogMessage(".*Ready to accept connections.*\\n", 1))
				.withExposedPorts(3306)
				.withReuse(true);

		MYSQL_CONTAINER.start();
		MYSQL_CONTAINER.setEnv(List.of());

		System.setProperty("spring.redis.host", MYSQL_CONTAINER.getHost());
		System.setProperty("spring.redis.port", MYSQL_CONTAINER.getMappedPort(6379).toString());
	}
}
