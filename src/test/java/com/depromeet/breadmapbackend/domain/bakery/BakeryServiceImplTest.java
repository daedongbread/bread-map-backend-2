package com.depromeet.breadmapbackend.domain.bakery;

import static java.time.LocalDate.*;
import static org.assertj.core.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.jdbc.Sql;

import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryDto;
import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryScoreBaseWithSelectedDate;
import com.depromeet.breadmapbackend.domain.bakery.view.BakeryViewId;
import com.depromeet.breadmapbackend.domain.bakery.view.BakeryViewRepository;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;

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

	@Autowired
	private EntityManager em;

	@Autowired
	private DataSource dataSource;

	private final Long bakeryId = 100L;

	@BeforeEach
	void setUp() throws Exception {
		try (final Connection connection = dataSource.getConnection()) {

			final String sql = """
						insert into bakery_view (bakery_id, view_date, view_count) values
						(200, 'date1', count200),
						(300, 'date1', count300),
						(500, 'date1', count100),
						(600, 'date1', count50),
						(200, 'date2', count300),
						(300, 'date2', count100),
						(500, 'date2', count200),
						(600, 'date2', count200)
				""".replaceAll("date1", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
					.replaceAll("date2", LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
						.replaceAll("count100", "100")
						.replaceAll("count200", "200")
						.replaceAll("count300", "300")
						.replaceAll("count50", "50");
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.executeUpdate();
		}
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
	@Sql("classpath:scoredBakery-test-data.sql")
	void 빵집_점수_조회() throws Exception{
	    //given
		//when
		final List<BakeryScoreBaseWithSelectedDate> bakeryScoreBaseList = sut.getBakeriesScoreFactors();

		final List<FlagBakery> resultList = em.createQuery(
				"select fb from FlagBakery fb where fb.bakery.id = :bakeryId ", FlagBakery.class)
			.setParameter("bakeryId", 100L)
			.getResultList();



		//then
		assertThat(bakeryScoreBaseList.size()).isEqualTo(11);
		final BakeryScoreBaseWithSelectedDate firstBakeryScore =
			bakeryScoreBaseList.stream()
			.filter(b -> b.bakery().getId().equals(100L))
			.findFirst()
			.get();
		assertThat(firstBakeryScore.bakeryRating()).isEqualTo(3D);
		assertThat(firstBakeryScore.flagCount()).isEqualTo(1L);
		assertThat(firstBakeryScore.viewCount()).isEqualTo(0L);

		final BakeryScoreBaseWithSelectedDate secondBakeryScore =
			bakeryScoreBaseList.stream()
				.filter(b -> b.bakery().getId().equals(300L))
				.findFirst()
				.get();

		assertThat(secondBakeryScore.bakeryRating()).isEqualTo(4D);
		assertThat(secondBakeryScore.flagCount()).isEqualTo(1L);
		assertThat(secondBakeryScore.viewCount()).isEqualTo(400L);

		final BakeryScoreBaseWithSelectedDate thirdBakeryScore =
			bakeryScoreBaseList.stream()
				.filter(b -> b.bakery().getId().equals(700L))
				.findFirst()
				.get();

		assertThat(thirdBakeryScore.bakeryRating()).isEqualTo(2.5D);
		assertThat(thirdBakeryScore.flagCount()).isEqualTo(0L);
		assertThat(thirdBakeryScore.viewCount()).isEqualTo(0L);

	}

}