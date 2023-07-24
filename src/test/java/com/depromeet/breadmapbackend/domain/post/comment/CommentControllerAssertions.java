package com.depromeet.breadmapbackend.domain.post.comment;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.ResultActions;

/**
 * PostControllerAssertions
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/23
 */
public class CommentControllerAssertions {

	public static void 댓글_추가_요청_결과_검증(final ResultActions 결과) throws Exception {
		결과.andExpect(status().isCreated());
	}

	public static void 댓글_수정_요청_결과_검증(final ResultActions 결과) throws Exception {
		결과.andExpect(status().isOk());
	}

	public static void 빵_이야기_댓글_조회_요청_결과_검증(final ResultActions 결과) throws Exception {
		결과.andExpect(status().isOk());
	}

	public static void 댓글_삭제_요청_결과_검증(final ResultActions 결과) throws Exception {
		결과.andExpect(status().isOk());
	}

	public static void 커뮤니티_글_댓글_좋아요_요청_결과_검증(final ResultActions 결과) throws Exception {
		결과.andExpect(status().isOk());
	}



}
