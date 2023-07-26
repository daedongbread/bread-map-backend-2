package com.depromeet.breadmapbackend.domain.admin.feed;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class FeedServiceTestImpl {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Test
	@DisplayName("")
	void test() {

		redisTemplate.opsForZSet().add("test key", "test value1", 1);
		redisTemplate.opsForZSet().add("test key", "test value2", 2);
		redisTemplate.opsForZSet().add("test key", "test value3", 3);

		//given
		Set<String> testKey = redisTemplate.opsForZSet()
			.reverseRange("test key2", 0, 3);

		for (String str : testKey) {
			System.out.println(str);
		}

		//when

		//then
	}

}
