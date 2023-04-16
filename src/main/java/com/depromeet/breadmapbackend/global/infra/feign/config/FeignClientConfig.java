package com.depromeet.breadmapbackend.global.infra.feign.config;

import com.depromeet.breadmapbackend.global.infra.feign.exception.FeignError;
import feign.Logger;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {
    @Bean
    Logger.Level githubFeignClientLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignError();
    }
}
