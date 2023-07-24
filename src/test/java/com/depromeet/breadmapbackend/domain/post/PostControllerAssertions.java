package com.depromeet.breadmapbackend.domain.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.ResultActions;

/**
 * PostControllerAssertions
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/23
 */
public class PostControllerAssertions {

	public static void 커뮤니티_추천_조회_요청_결과_검증(final ResultActions 결과) throws Exception {
		결과.andExpect(status().isOk());
	}

	public static void 커뮤니티_전체_카테고리_조회_요청_결과_검증(final ResultActions 결과) throws Exception {
		결과.andExpect(status().isOk());
	}

	public static void 빵_이야기_상세_조회_요청_결과_검증(final ResultActions 결과) throws Exception {
		결과.andExpect(status().isOk());
	}

	public static void 빵_이야기_작성_요청_결과_검증(final ResultActions 결과) throws Exception {
		결과.andExpect(status().isCreated());
	}

	public static void 커뮤니티_글_좋아요_요청_결과_검증(final ResultActions 결과) throws Exception {
		결과.andExpect(status().isOk());
	}

	public static void 커뮤니티_글_수정_요청_결과_검증(final ResultActions 결과) throws Exception {
		결과.andExpect(status().isOk());
	}

}
