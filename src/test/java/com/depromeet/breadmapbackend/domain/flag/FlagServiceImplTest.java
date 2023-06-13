package com.depromeet.breadmapbackend.domain.flag;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import com.depromeet.breadmapbackend.domain.flag.dto.FlagDto;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

class FlagServiceImplTest extends FlagServiceTest {

	@Autowired
	private FlagServiceImpl sut;

	@Test
	@Sql("classpath:flag-test-data.sql")
	void User_Flag삭제_성공_테스트() throws Exception {
		// given
		final String oAuthId = "APPLE_111";
		final Long flagId = 111L;
		final Long bakeryId = 111L;
		assertThat(getFlagBakeryList(oAuthId, flagId, bakeryId)).hasSize(1);

		// when
		sut.removeBakeryToFlag(oAuthId, flagId, bakeryId);

		// then
		assertThat(getFlagBakeryList(oAuthId, flagId, bakeryId)).isEmpty();
	}

	@ParameterizedTest
	@MethodSource("CreateRemoveBakeryToFlagTestSource")
	@Sql("classpath:flag-test-data.sql")
	void User_Flag삭제_실패_테스트(
		final String oAuthId,
		final Long flagId,
		final Long bakeryId,
		final DaedongStatus daedongStatus
	) throws Exception {

		final Throwable thrown = catchThrowable(() -> sut.removeBakeryToFlag(oAuthId, flagId, bakeryId));
		assertThat(thrown).isInstanceOf(DaedongException.class);
		assertThat(((DaedongException)thrown).getDaedongStatus()).isEqualTo(daedongStatus);
	}

	@Test
	@Sql("classpath:flag-test-data.sql")
	void flag_조회_성공_테스트() throws Exception {
		//given
		final Long userId = 111L;

		//when
		final List<FlagDto> flags = sut.getFlags(userId);

		//then
		assertThat(flags).hasSize(2);

		final FlagDto firstFlag = flags.get(0);
		final FlagDto secondFlag = flags.get(1);

		assertThat(firstFlag.getFlagInfo().getId()).isEqualTo(111L);
		assertThat(firstFlag.getFlagInfo().getColor()).isEqualTo(FlagColor.YELLOW);
		assertThat(firstFlag.getBakeryImageList()).isEqualTo(List.of("image111"));
		assertThat(secondFlag.getFlagInfo().getId()).isEqualTo(112L);
		assertThat(secondFlag.getFlagInfo().getColor()).isEqualTo(FlagColor.CYAN);
		assertThat(secondFlag.getBakeryImageList()).isEqualTo(List.of("image", "image2"));

	}

	private List<FlagBakery> getFlagBakeryList(final String oAuthId, final Long flagId, final Long bakeryId) {
		return em.createQuery(
				"select f "
					+ "from FlagBakery f "
					+ "where f.flag.id = :flagId "
					+ "and f.bakery.id = :bakeryId "
					+ "and f.user.oAuthInfo.oAuthId = :oAuthId ", FlagBakery.class)
			.setParameter("flagId", flagId)
			.setParameter("bakeryId", bakeryId)
			.setParameter("oAuthId", oAuthId)
			.getResultList();
	}

	private static Stream<Arguments> CreateRemoveBakeryToFlagTestSource() {
		// given
		final String oAuthId = "APPLE_111";
		final String wrongOAuthId = "WRONG_OAUTH_ID";

		final Long flagId = 111L;
		final Long wrongFlagId = 999L;
		final Long otherUserFlagId = 113L;

		final Long bakeryId = 111L;
		final Long wrongBakeryId = 999L;
		final Long differentBakeryId = 113L;

		return Stream.of(
			Arguments.of(wrongOAuthId, flagId, bakeryId, DaedongStatus.USER_NOT_FOUND),
			Arguments.of(oAuthId, wrongFlagId, bakeryId, DaedongStatus.FLAG_NOT_FOUND),
			Arguments.of(oAuthId, otherUserFlagId, bakeryId, DaedongStatus.FLAG_NOT_FOUND),
			Arguments.of(oAuthId, flagId, wrongBakeryId, DaedongStatus.BAKERY_NOT_FOUND),
			Arguments.of(oAuthId, flagId, differentBakeryId, DaedongStatus.BAKERY_NOT_FOUND)

		);
	}
}