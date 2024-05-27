package com.depromeet.breadmapbackend.global.infra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;

@Slf4j
@Profile({"default", "local"})
@Configuration
@RequiredArgsConstructor
public class LocalMysqlConfig {
    static {
        ComposeContainer MYSQL_CONTAINER = new ComposeContainer(new File("src/main/resources/local-mysql/docker-compose.yml"))
                .withExposedService("local-mysql", 33065, Wait.forHealthcheck());
        MYSQL_CONTAINER.start();
    }
}
