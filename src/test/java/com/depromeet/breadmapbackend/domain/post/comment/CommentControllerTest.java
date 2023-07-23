package com.depromeet.breadmapbackend.domain.post.comment;

import static com.depromeet.breadmapbackend.domain.post.comment.CommentControllerAssertions.*;
import static com.depromeet.breadmapbackend.domain.post.comment.CommentControllerTestSteps.*;

import java.sql.Connection;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import com.depromeet.breadmapbackend.domain.post.comment.dto.request.CommentCreateRequest;
import com.depromeet.breadmapbackend.domain.post.comment.dto.request.CommentUpdateRequest;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.utils.ControllerTest;

/**
 * PostControllerTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/22
 */
@DisplayName("Post(커뮤니티) controller 테스트")
class CommentControllerTest extends ControllerTest {

	private String 사용자_토큰;

	@Autowired
	private DataSource dataSource;

	@BeforeEach
	void setUp() throws Exception {
		setUpTestDate();
		사용자_토큰 = jwtTokenProvider.createJwtToken("APPLE_111", RoleType.USER.getCode()).getAccessToken();
	}

	@Test
	void 댓글_등록() throws Exception {
		// given
		final var 댓글_데이터 = new CommentCreateRequest(222L, "내용", true, 0L);

		// when
		final var 결과 = 댓글_추가_요청(댓글_데이터, 사용자_토큰, mockMvc, objectMapper);

		// then
		댓글_추가_요청_결과_검증(결과);

	}

	@Test
	void 댓글_조회() throws Exception {
		// given
		final var 빵_이야기_고유_번호 = 222L;

		// when
		final var 결과 = 빵_이야기_댓글_조회_요청(빵_이야기_고유_번호, 0, 사용자_토큰, mockMvc);

		//then
		빵_이야기_댓글_조회_요청_결과_검증(결과);
	}

	@Test
	void 댓글_수정() throws Exception {
		// given
		final var 댓글_수정_요청_데이터 = new CommentUpdateRequest(111L, "수정된 내용");

		// when
		final var 결과 = 댓글_수정_요청(댓글_수정_요청_데이터, 사용자_토큰, mockMvc, objectMapper);

		// then
		커뮤니티_전체_카테고리_조회_요청_결과_검증(결과);
	}

	@Test
	void 댓글_삭제() throws Exception {
		// given
		final Long 삭제할_댓글_id = 111L;
		// when

		final var 결과 = 댓글_삭제_요청(삭제할_댓글_id, 사용자_토큰, mockMvc);

		// then
		댓글_삭제_요청_결과_검증(결과);
	}

	private void setUpTestDate() throws Exception {
		try (final Connection connection = dataSource.getConnection()) {
			ScriptUtils.executeSqlScript(connection, new ClassPathResource("comment-test-data.sql"));

		}
	}

}