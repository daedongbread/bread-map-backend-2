package com.depromeet.breadmapbackend.global.infra;

import com.depromeet.breadmapbackend.global.infra.properties.CustomRedisProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Slf4j
@Profile({"prod", "stage", "local"})
@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final CustomRedisProperties customRedisProperties;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        log.info("Connect to Redis");
        return new LettuceConnectionFactory(customRedisProperties.getHost(), customRedisProperties.getPort());  // Lettuce 사용
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());   // Key: String
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));  // Value: 직렬화에 사용할 Object 사용하기
        return redisTemplate;
    }
}