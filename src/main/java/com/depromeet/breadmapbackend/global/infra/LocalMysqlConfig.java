package com.depromeet.breadmapbackend.global.infra;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

@Configuration
@Slf4j
class LocalMysqlConfig {
    static {
        if (System.getProperty("spring.profiles.active").contains("local")) {
            DockerImageName MYSQL_IMAGE = DockerImageName.parse("mysql:8.0.36");
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
}
