package com.depromeet.breadmapbackend.domain.flag;

import static org.assertj.core.api.Assertions.*;

import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import com.depromeet.breadmapbackend.domain.flag.dto.FlagBakeryDto;
import com.depromeet.breadmapbackend.domain.flag.dto.FlagDto;
import com.depromeet.breadmapbackend.domain.flag.dto.FlagRequest;
import com.depromeet.breadmapbackend.domain.review.dto.MapSimpleReviewDto;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

@SpringBootTest
class FlagServiceImplTest {

	@Autowired
	private FlagServiceImpl sut;
	@Autowired
	private EntityManager em;
	@Autowired
	private DataSource dataSource;

	@BeforeEach
	void setUp() throws Exception {
		try (final Connection connection = dataSource.getConnection()) {

			ScriptUtils.executeSqlScript(connection, new ClassPathResource("flag-test-data.sql"));
		}
	}

	@Test
		// @Sql("classpath:flag-test-data.sql")
	void User_Flag삭제_성공_테스트() throws Exception {
		// given
		final Long userId = 111L;
		final Long flagId = 111L;
		final Long bakeryId = 111L;
		assertThat(getFlagBakeryList(userId, flagId, bakeryId)).hasSize(1);

		// when
		sut.removeBakeryToFlag(userId, flagId, bakeryId);

		// then
		assertThat(getFlagBakeryList(userId, flagId, bakeryId)).isEmpty();
	}

	@ParameterizedTest
	@MethodSource("CreateRemoveBakeryToFlagTestSource")
		// @Sql("classpath:flag-test-data.sql")
	void User_Flag삭제_실패_테스트(
		final Long userId,
		final Long flagId,
		final Long bakeryId,
		final DaedongStatus daedongStatus
	) throws Exception {

		final Throwable thrown = catchThrowable(() -> sut.removeBakeryToFlag(userId, flagId, bakeryId));
		assertThat(thrown).isInstanceOf(DaedongException.class);
		assertThat(((DaedongException)thrown).getDaedongStatus()).isEqualTo(daedongStatus);
	}

	@Test
		// @Sql("classpath:flag-test-data.sql")
	void flag_조회_성공_테스트() throws Exception {
		//given
		final Long userId = 111L;

		//when
		final List<FlagDto> flags = sut.getFlags(userId);

		//then
		assertThat(flags).hasSize(3);

		final FlagDto firstFlag = flags.get(1);
		final FlagDto secondFlag = flags.get(2);

		assertThat(firstFlag.getFlagInfo().getId()).isEqualTo(111L);
		assertThat(firstFlag.getFlagInfo().getColor()).isEqualTo(FlagColor.YELLOW);
		assertThat(firstFlag.getBakeryImageList()).isEqualTo(List.of("image111"));
		assertThat(secondFlag.getFlagInfo().getId()).isEqualTo(112L);
		assertThat(secondFlag.getFlagInfo().getColor()).isEqualTo(FlagColor.CYAN);
		assertThat(secondFlag.getBakeryImageList()).isEqualTo(List.of("image", "image2"));

	}

	@Test
		// @Sql("classpath:flag-test-data.sql")
	void flag_추가_성공_테스트() throws Exception {
		//given
		final Long userId = 111L;
		final String flagName = "가즈아아아아";
		final FlagColor flagColor = FlagColor.RED;

		final FlagRequest request =
			FlagRequest.builder()
				.name(flagName)
				.color(flagColor)
				.build();
		//when
		sut.addFlag(userId, request);

		//then
		final Flag savedFlag = em.createQuery("select f "
				+ "from Flag f "
				+ "where f.user.id = :userId "
				+ "and f.name = :name "
				+ "and f.color = :color ", Flag.class)
			.setParameter("userId", userId)
			.setParameter("name", flagName)
			.setParameter("color", flagColor)
			.getSingleResult();
		assertThat(savedFlag.getId()).isNotNull();

	}

	@Test
		// @Sql("classpath:flag-test-data.sql")
	void flag_수정_성공_테스트() throws Exception {
		//given
		final Long userId = 111L;
		final Long flagId = 112L;
		final String flagName = "갈까??";
		final FlagColor flagColor = FlagColor.SKY;

		final FlagRequest request =
			FlagRequest.builder()
				.name(flagName)
				.color(flagColor)
				.build();
		//when
		sut.updateFlag(userId, flagId, request);

		//then
		final Flag updated = em.createQuery("select f "
				+ "from Flag f "
				+ "where f.id =: id", Flag.class)
			.setParameter("id", flagId)
			.getSingleResult();

		assertThat(updated.getName()).isEqualTo(flagName);
		assertThat(updated.getColor()).isEqualTo(flagColor);
	}

	@Test
		// @Sql("classpath:flag-test-data.sql")
	void flag_수정_실패_테스트_FLAG_UNEDIT_EXCEPTION() throws Exception {
		//given
		final Long userId = 111L;
		final Long flagId = 100L;
		final String flagName = "갈까??";
		final FlagColor flagColor = FlagColor.SKY;

		final FlagRequest request =
			FlagRequest.builder()
				.name(flagName)
				.color(flagColor)
				.build();
		//when
		//then
		final Throwable thrown = catchThrowable(() -> sut.updateFlag(userId, flagId, request));
		assertThat(thrown).isInstanceOf(DaedongException.class);
		assertThat(((DaedongException)thrown).getDaedongStatus())
			.isEqualTo(DaedongStatus.FLAG_UNEDIT_EXCEPTION);
	}

	@Test
		// @Sql("classpath:flag-test-data.sql")
	void falg_제거_성공_테스트() throws Exception {
		//given
		final Long userId = 111L;
		final Long flagId = 112L;

		//when
		sut.removeFlag(userId, flagId);

		//then
		assertThatThrownBy(() -> em.createQuery("select f "
				+ "from Flag f "
				+ "where f.id =: id", Flag.class)
			.setParameter("id", flagId)
			.getSingleResult())
			.isInstanceOf(NoResultException.class);
	}

	@Test
		// @Sql("classpath:flag-test-data.sql")
	void flag에_등록된_bakery_조회_성공_테스트() throws Exception {
		//given
		final Long userId = 111L;
		final Long flagId = 112L;

		//when
		final FlagBakeryDto result = sut.getBakeriesByFlag(userId, flagId);

		//then
		assertThat(result.getFlagInfo().getBakeryNum()).isEqualTo(2);
		assertThat(result.getFlagInfo().getName()).isEqualTo("flag1");
		assertThat(result.getFlagInfo().getColor()).isEqualTo(FlagColor.CYAN);

		assertThat(result.getFlagBakeryInfoList()).hasSize(2);
		final FlagBakeryDto.FlagBakeryInfo firstBakery = result.getFlagBakeryInfoList().get(0);
		assertThat(firstBakery.getId()).isEqualTo(113L);
		assertThat(firstBakery.getRating()).isEqualTo(2.6666666666666665d);
		assertThat(firstBakery.getReviewNum()).isEqualTo(3L);
		assertThat(firstBakery.getSimpleReviewList()).hasSize(3);
		assertThat(firstBakery.getSimpleReviewList()
			.stream()
			.map(MapSimpleReviewDto::getContent)
			.collect(Collectors.toList())
		).isEqualTo(List.of("제주도", "서울", "수원"));
	}

	@Test
		// @Sql("classpath:flag-test-data.sql")
	void bakery_flag에_추가_성공_테스트() throws Exception {
		//given
		final Long userId = 111L;
		final Long flagId = 111L;
		final Long bakeryId = 113L;

		//when
		sut.addBakeryToFlag(userId, flagId, bakeryId);
		//then
		final FlagBakery singleResult = em.createQuery("select fb "
				+ "from FlagBakery fb "
				+ "where fb.flag.id = :flagId "
				+ "and fb.user.id = :userId "
				+ "and fb.bakery.id = :bakeryId ", FlagBakery.class)
			.setParameter("flagId", flagId)
			.setParameter("userId", userId)
			.setParameter("bakeryId", bakeryId)
			.getSingleResult();

		assertThat(singleResult.getId()).isNotNull();
	}

	@Test
		// @Sql("classpath:flag-test-data.sql")
	void bakery_flag에_추가_실패_테스트_FLAG_BAKERY_DUPLICATE_EXCEPTION() throws Exception {
		//given
		final Long userId = 111L;
		final Long flagId = 111L;
		final Long bakeryId = 111L;

		//when

		//then
		final Throwable thrown = catchThrowable(() -> sut.addBakeryToFlag(userId, flagId, bakeryId));
		assertThat(thrown).isInstanceOf(DaedongException.class);
		assertThat(((DaedongException)thrown).getDaedongStatus())
			.isEqualTo(DaedongStatus.FLAG_BAKERY_DUPLICATE_EXCEPTION);

	}

	private List<FlagBakery> getFlagBakeryList(final Long userId, final Long flagId, final Long bakeryId) {
		return em.createQuery(
				"select f "
					+ "from FlagBakery f "
					+ "where f.flag.id = :flagId "
					+ "and f.bakery.id = :bakeryId "
					+ "and f.user.id = :userId ", FlagBakery.class)
			.setParameter("flagId", flagId)
			.setParameter("bakeryId", bakeryId)
			.setParameter("userId", userId)
			.getResultList();
	}

	private static Stream<Arguments> CreateRemoveBakeryToFlagTestSource() {
		// given
		final Long userId = 111L;
		final Long wrongUserId = 999999L;

		final Long flagId = 111L;
		final Long wrongFlagId = 999L;
		final Long otherUserFlagId = 113L;

		final Long bakeryId = 111L;
		final Long wrongBakeryId = 999L;
		final Long differentBakeryId = 113L;

		return Stream.of(
			Arguments.of(wrongUserId, flagId, bakeryId, DaedongStatus.BAKERY_NOT_FOUND),
			Arguments.of(userId, wrongFlagId, bakeryId, DaedongStatus.BAKERY_NOT_FOUND),
			Arguments.of(userId, otherUserFlagId, bakeryId, DaedongStatus.BAKERY_NOT_FOUND),
			Arguments.of(userId, flagId, wrongBakeryId, DaedongStatus.BAKERY_NOT_FOUND),
			Arguments.of(userId, flagId, differentBakeryId, DaedongStatus.BAKERY_NOT_FOUND)

		);
	}
}