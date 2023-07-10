package com.depromeet.breadmapbackend.domain.bakery;

import static java.time.LocalDate.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import com.depromeet.breadmapbackend.domain.bakery.dto.BakeryDto;
import com.depromeet.breadmapbackend.domain.bakery.view.BakeryView;
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


	@Test
	@Sql("classpath:bakery-test-data.sql")
	void 최초조회() throws Exception{
	    //given
		final Long userId = 111L;
		final Long bakeryId = 100L;

	    //when
		final BakeryDto result = sut.getBakery(userId, bakeryId);

		//then
		assertThat(result).isNotNull();
		Thread.sleep(10000);
		assertThat(repository.findById(new BakeryViewId(bakeryId, now())).get().getViewCount()).isEqualTo(1L);

	}
}