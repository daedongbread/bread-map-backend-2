package com.depromeet.breadmapbackend.infra.feign.config;

import com.depromeet.breadmapbackend.infra.feign.exception.SgisFeignError;
import feign.Logger;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SgisClientConfig {
    @Bean
    Logger.Level githubFeignClientLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new SgisFeignError();
    }
}
