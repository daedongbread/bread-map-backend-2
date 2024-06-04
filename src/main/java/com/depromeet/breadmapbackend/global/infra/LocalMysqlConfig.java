package com.depromeet.breadmapbackend.global.infra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

@Slf4j
@Profile({"default", "local"})
@Configuration
@RequiredArgsConstructor
public class LocalMysqlConfig {

    private static final DockerImageName MYSQL_IMAGE = DockerImageName.parse("mysql:8.0.36");

    static {
        GenericContainer<?> MYSQL_CONTAINER = new GenericContainer<>(MYSQL_IMAGE)
                .withExposedPorts(3306)
                .withEnv(Map.of(
                        "MYSQL_DATABASE", "local_bread_map",
                        "MYSQL_USER", "admin",
                        "MYSQL_ROOT_PASSWORD", "1234",
                        "MYSQL_PASSWORD", "1234"
                ));
        MYSQL_CONTAINER.start();
        String jdbcUrl = String.format("jdbc:mysql://localhost:%s/local_bread_map", MYSQL_CONTAINER.getMappedPort(3306));
        System.setProperty("spring.datasource.url", jdbcUrl);
        log.info("Generated JDBC URL: {}", jdbcUrl);
    }
}
