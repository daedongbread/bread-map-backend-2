package com.depromeet.breadmapbackend.domain.bakery;

import static java.time.LocalDate.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.jdbc.Sql;

import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryDto;
import com.depromeet.breadmapbackend.domain.bakery.view.BakeryViewId;
import com.depromeet.breadmapbackend.domain.bakery.view.BakeryViewRepository;

/**
 * BakeryServiceImplTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/10
 */


@SpringBootTest
class BakeryServiceImplTest  {

	@Autowired
	private BakeryService sut;

	@Autowired
	private BakeryViewRepository repository;

	@Autowired
	private StringRedisTemplate redisTemplate;

	private final Long bakeryId = 100L;

	@BeforeEach
	void setUp() {
		redisTemplate.opsForValue().getAndDelete("BAKERY-VIEW:" + bakeryId + ":" + LocalDate.now());
	}

	@Test
	@Sql("classpath:bakery-test-data.sql")
	void 최초조회() throws Exception{
		//given
		final Long userId = 111L;

	    //when
		final BakeryDto result = sut.getBakery(userId, bakeryId);

		//then
		assertThat(result).isNotNull();
		Thread.sleep(1000);
		assertThat(repository.findById(new BakeryViewId(bakeryId, now())).get().getViewCount()).isEqualTo(1L);
	}

	@Test
	@Sql("classpath:bakery-test-data.sql")
	void 동시_조회() throws Exception {
		//given
		final Long userId = 111L;
		final int threadCount = 500;
		final ExecutorService executorService = Executors.newFixedThreadPool(16);
		final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
		//when
		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					sut.getBakery(userId, bakeryId);
				} finally {
					countDownLatch.countDown();
				}
			});
		}
		countDownLatch.await();
		Thread.sleep(2000);

		assertThat(repository.findById(
			new BakeryViewId(bakeryId, now())
		).get().getViewCount()).isEqualTo(500L);
	}
	
	@Test
	void 빵집_점수_조회() throws Exception{
	    //given
	    
	    
	    //when
	    
	    //then
	    
	}

}