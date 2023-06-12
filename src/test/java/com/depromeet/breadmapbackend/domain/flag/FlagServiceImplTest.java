package com.depromeet.breadmapbackend.domain.flag;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

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

}