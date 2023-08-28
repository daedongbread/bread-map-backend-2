package com.depromeet.breadmapbackend.domain.report;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.ResultActions;

/**
 * PostControllerAssertions
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/23
 */
public class ReportControllerAssertions {

	public static void 커뮤니티_신고_요청_결과_검증(final ResultActions 결과) throws Exception {
		결과.andExpect(status().isCreated());
	}

}
