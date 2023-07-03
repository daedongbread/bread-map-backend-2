package com.depromeet.breadmapbackend.global.util;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * CalenderUtilTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/03
 */
class CalenderUtilTest {

	@Test
	void weekOfMonthTest() throws Exception {
		//given
		final LocalDate targetDate1 = LocalDate.of(2021, 5, 1);
		final LocalDate targetDate2 = LocalDate.of(2021, 5, 2);

		//when
		final String result1 = CalenderUtil.getYearWeekOfMonth(targetDate1);
		final String result2 = CalenderUtil.getYearWeekOfMonth(targetDate2);

		//then
		assertThat(result1).isEqualTo("2021-5-1");
		assertThat(result2).isEqualTo("2021-5-2");

	}
}