package com.depromeet.breadmapbackend.domain.bakery;

import static java.time.LocalDate.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
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
import com.depromeet.breadmapbackend.domain.bakery.dto.NewBakeryCardDto;
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
class BakeryServiceImplTest {

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

//	@Test
//	@Sql("classpath:bakery-test-data.sql")
//	void 최초조회() throws Exception {
//		//given
//		final Long userId = 111L;
//
//		//when
//		final BakeryDto result = sut.getBakery(userId, bakeryId);
//
//		//then
//		assertThat(result).isNotNull();
//		Thread.sleep(1000);
//		assertThat(repository.findById(new BakeryViewId(bakeryId, now())).get().getViewCount()).isEqualTo(1L);
//	}
//
//	@Test
//	@Sql("classpath:bakery-test-data.sql")
//	void 동시_조회() throws Exception {
//		//given
//		final Long userId = 111L;
//		final int threadCount = 500;
//		final ExecutorService executorService = Executors.newFixedThreadPool(16);
//		final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
//		//when
//		for (int i = 0; i < threadCount; i++) {
//			executorService.submit(() -> {
//				try {
//					sut.getBakery(userId, bakeryId);
//				} finally {
//					countDownLatch.countDown();
//				}
//			});
//		}
//		countDownLatch.await();
//		Thread.sleep(2000);
//
//		assertThat(repository.findById(
//			new BakeryViewId(bakeryId, now())
//		).get().getViewCount()).isEqualTo(500L);
//	}

	@Test
	@Sql("classpath:bakery-test-data.sql")
	void 신상_빵집_조회() throws Exception {
		//given
		final Long userId = 111L;
		//when
		final List<NewBakeryCardDto> newBakeryList = sut.getNewBakeryList(userId);
		//then
		assertThat(newBakeryList).isNotNull();
		assertThat(newBakeryList.size()).isEqualTo(10);
		assertThat(newBakeryList.stream().map(NewBakeryCardDto::id))
			.containsExactly(900L, 800L, 300L, 600L, 200L, 1100L, 1200L, 500L, 700L, 100L);
		final NewBakeryCardDto bakery200 = newBakeryList.stream().filter(newBakery -> newBakery.id().equals(200L))
			.findFirst().get();
		assertThat(bakery200.isFlagged()).isTrue();
		assertThat(bakery200.isFollowed()).isFalse();
		final NewBakeryCardDto bakery300 = newBakeryList.stream().filter(newBakery -> newBakery.id().equals(300L))
			.findFirst().get();
		assertThat(bakery300.isFlagged()).isFalse();
		assertThat(bakery300.isFollowed()).isTrue();
		final NewBakeryCardDto bakery600 = newBakeryList.stream().filter(newBakery -> newBakery.id().equals(600L))
			.findFirst().get();
		assertThat(bakery600.isFlagged()).isTrue();
		assertThat(bakery600.isFollowed()).isFalse();
	}
}