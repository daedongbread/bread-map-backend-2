package com.depromeet.breadmapbackend.domain.bakery;

import static org.assertj.core.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryScoreBaseWithSelectedDate;
import com.depromeet.breadmapbackend.domain.bakery.dto.NewBakeryCardDto;
import com.depromeet.breadmapbackend.domain.bakery.view.BakeryViewRepository;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
import com.depromeet.breadmapbackend.utils.TestLocalStackConfig;

/**
 * BakeryServiceImplTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/10
 */

@SpringBootTest
@Import({TestLocalStackConfig.class})
class BakeryServiceImplTest {

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

			ScriptUtils.executeSqlScript(connection, new ClassPathResource("scoredBakery-test-data.sql"));
			insertBakeryViewTestData(connection);
			// insertReviewProductRatingTestData(connection);
			insertFlagBakeryTestData(connection);
		}
		redisTemplate.opsForValue().getAndDelete("BAKERY-VIEW:" + bakeryId + ":" + LocalDate.now());
	}

	// @Test
	// @Sql("classpath:bakery-test-data.sql")
	// void 최초조회() throws Exception {
	// 	//given
	// 	final Long userId = 111L;
	//
	// 	//when
	// 	final BakeryDto result = sut.getBakery(userId, bakeryId);
	//
	// 	//then
	// 	assertThat(result).isNotNull();
	// 	Thread.sleep(1000);
	// 	assertThat(repository.findById(new BakeryViewId(bakeryId, now())).get().getViewCount()).isEqualTo(1L);
	// }
	//
	// @Test
	// @Sql("classpath:bakery-test-data.sql")
	// void 동시_조회() throws Exception {
	// 	//given
	// 	final Long userId = 111L;
	// 	final int threadCount = 500;
	// 	final ExecutorService executorService = Executors.newFixedThreadPool(16);
	// 	final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
	// 	//when
	// 	for (int i = 0; i < threadCount; i++) {
	// 		executorService.submit(() -> {
	// 			try {
	// 				sut.getBakery(userId, bakeryId);
	// 			} finally {
	// 				countDownLatch.countDown();
	// 			}
	// 		});
	// 	}
	// 	countDownLatch.await();
	// 	Thread.sleep(2000);
	//
	// 	assertThat(repository.findById(
	// 		new BakeryViewId(bakeryId, now())
	// 	).get().getViewCount()).isEqualTo(500L);
	// }

	@Test
	void 빵집_점수_조회() throws Exception {
		//given
		//when
		final List<BakeryScoreBaseWithSelectedDate> bakeryScoreBaseList = sut.getBakeriesScoreFactors(LocalDate.now());

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
		// assertThat(firstBakeryScore.bakeryRating()).isEqualTo(3D);
		assertThat(firstBakeryScore.flagCount()).isEqualTo(1L);
		assertThat(firstBakeryScore.viewCount()).isEqualTo(0L);

		final BakeryScoreBaseWithSelectedDate secondBakeryScore =
			bakeryScoreBaseList.stream()
				.filter(b -> b.bakery().getId().equals(300L))
				.findFirst()
				.get();

		// assertThat(secondBakeryScore.bakeryRating()).isEqualTo(4D);
		assertThat(secondBakeryScore.flagCount()).isEqualTo(1L);
		assertThat(secondBakeryScore.viewCount()).isEqualTo(400L);

		final BakeryScoreBaseWithSelectedDate thirdBakeryScore =
			bakeryScoreBaseList.stream()
				.filter(b -> b.bakery().getId().equals(700L))
				.findFirst()
				.get();

		// assertThat(thirdBakeryScore.bakeryRating()).isEqualTo(2.5D);
		assertThat(thirdBakeryScore.flagCount()).isEqualTo(0L);
		assertThat(thirdBakeryScore.viewCount()).isEqualTo(0L);

	}

	@Test
	void 신상_빵집_조회() throws Exception {
		//given
		final Long userId = 111L;
		//when
		final List<NewBakeryCardDto> newBakeryList = sut.getNewBakeryList(userId);
		//then
		assertThat(newBakeryList).isNotNull();
		assertThat(newBakeryList.size()).isEqualTo(4);
		assertThat(newBakeryList.stream().map(NewBakeryCardDto::id))
			.containsExactly(300L, 200L, 500L, 100L);
		final NewBakeryCardDto bakery200 = newBakeryList.stream().filter(newBakery -> newBakery.id().equals(200L))
			.findFirst().get();
		assertThat(bakery200.isFlagged()).isTrue();
		assertThat(bakery200.isFollowed()).isFalse();
		final NewBakeryCardDto bakery300 = newBakeryList.stream().filter(newBakery -> newBakery.id().equals(300L))
			.findFirst().get();
		assertThat(bakery300.isFlagged()).isFalse();
		assertThat(bakery300.isFollowed()).isTrue();
	}

	private void insertBakeryViewTestData(final Connection connection) throws SQLException {
		final String sql = """
					insert into bakery_view (bakery_id, view_date, view_count) values
					(200, 'date1', 200),
					(300, 'date1', 300),
					(500, 'date1', 100),
					(600, 'date1', 50),
					(200, 'date2', 300),
					(300, 'date2', 100),
					(500, 'date2', 200),
					(600, 'date2', 200)
			""".replaceAll("date1", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
			.replaceAll("date2", LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.executeUpdate();
	}

	// private void insertReviewProductRatingTestData(final Connection connection) throws SQLException {
	// 	final String sql = """
	// 			insert into review_product_rating (id, created_at, modified_at, rating, bakery_id, product_id, review_id, user_id   )values
	// 			(111,  'date1', '2023-01-01', 4, 100, 5534, 111,112),
	// 		 	(112,  'date2', '2023-01-01', 3, 100, 5535 , 111, 112),
	// 			(113,  'date3', '2023-01-01', 2, 100, 5536 , 111, 112),
	// 			(114,  'date4', '2023-01-01', 2, 100, 5536 , 111, 112),-- 빵집 100 평균 평점 3
	//
	// 			(116,  'date1', '2023-01-01', 4, 300, 1505 , 112, 112), -- 빵집 300 평균 평점 4
	//
	// 		 	(117,  'date1', '2023-01-01', 4, 700, 5011 , 113, 112),
	// 		 	(118,  'date1', '2023-01-01', 2, 700, 5012 , 113, 112),
	// 		 	(119,  'date1', '2023-01-01', 1, 700, 5013 , 113, 112),
	// 		 	(120,  'date1', '2023-01-01', 3, 700, 5014 , 113, 112); -- 빵집 700 평균 평점 2.5
	// 		"""
	// 		.replaceAll(
	// 			"date1", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
	// 		)
	// 		.replaceAll(
	// 			"date2", LocalDateTime.now().minusDays(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
	// 		)
	// 		.replaceAll(
	// 			"date3", LocalDateTime.now().minusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
	// 		)
	// 		.replaceAll(
	// 			"date4", LocalDateTime.now().minusDays(8).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
	// 		);
	// 	PreparedStatement statement = connection.prepareStatement(sql);
	// 	statement.executeUpdate();
	// }

	private void insertFlagBakeryTestData(final Connection connection) throws SQLException {
		final String sql = """
					insert into FLAG_BAKERY (id, created_at, modified_at, bakery_id, flag_id, user_id )values
						(111,  'date1', '2023-01-01', 100, 111, 111),
						(112,  'date1', '2023-01-01', 200, 112, 111),
						(113,  'date1', '2023-01-01', 600, 112, 111),
						(114,  'date1', '2023-01-01', 300, 113, 112),
						(115,  'date1', '2023-01-01', 400, 113, 112),
						(116,  'date1', '2023-01-01', 500, 113, 112)
			""".replaceAll("date1", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.executeUpdate();
	}

}