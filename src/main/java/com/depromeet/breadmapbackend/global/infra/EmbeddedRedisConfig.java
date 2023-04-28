package com.depromeet.breadmapbackend.global.infra;

import com.depromeet.breadmapbackend.global.infra.properties.CustomRedisProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@Slf4j
@Profile({"default", "local"}) // TODO
@Configuration
@RequiredArgsConstructor
public class EmbeddedRedisConfig {
    private final CustomRedisProperties customRedisProperties;

    private RedisServer redisServer;

    @PostConstruct
    public void redisServer() throws IOException {
        log.info("Connect to Embedded-Redis");
        redisServer = RedisServer.builder()
                .port(customRedisProperties.getPort())
                .setting("maxmemory 128M")
                .build();
        redisServer.start();
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null && redisServer.isActive()) {
            redisServer.stop();
        }
    }
}
