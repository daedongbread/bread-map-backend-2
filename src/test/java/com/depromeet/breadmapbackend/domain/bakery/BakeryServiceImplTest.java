package com.depromeet.breadmapbackend.domain.bakery;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import com.depromeet.breadmapbackend.domain.bakery.dto.NewBakeryCardDto;

/**
 * BakeryServiceImplTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/06
 */
class BakeryServiceImplTest extends BakeryServiceTest {

	@Autowired
	private BakeryServiceImpl sut;

	@Test
	@Sql("classpath:bakery-test-data.sql")
	void 신상빵집_조회() throws Exception {
		//given
		final Long userId = 111L;

		//when
		final List<NewBakeryCardDto> newBakeryList = sut.getNewBakeryList(userId);

		//then
		assertThat(newBakeryList).hasSize(10);
	}
}