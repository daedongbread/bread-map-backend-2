package com.depromeet.breadmapbackend.domain.admin.post.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.ResultActions;

/**
 * PostAdminControllerAssertions
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/27
 */
public class PostAdminControllerAssertions {

	public static void 이벤트_조회_요청_결과_검증(final ResultActions 결과) throws Exception {
		결과.andExpect(status().isOk());
	}

	public static void 이벤트_등록_요청_결과_검증(final ResultActions 결과) throws Exception {
		결과.andExpect(status().isCreated());
	}

	public static void 이벤트_고정_가능여부_조회_결과_검증(final ResultActions 결과) throws Exception {
		결과.andExpect(status().isOk());
	}

	public static void 이벤트_수정_결과_검증(final ResultActions 결과) throws Exception {
		결과.andExpect(status().isOk());
	}

	public static void 이벤트_순서_수정_결과_검증(final ResultActions 결과) throws Exception {
		결과.andExpect(status().isOk());
	}

	public static void 이벤트_캐러셀_조회_결과_검증(final ResultActions 결과) throws Exception {
		결과.andExpect(status().isOk());
	}
}
