package com.depromeet.breadmapbackend.domain.admin.post.controller;

import static com.depromeet.breadmapbackend.domain.admin.post.controller.PostAdminControllerAssertions.*;
import static com.depromeet.breadmapbackend.domain.admin.post.controller.PostAdminControllerTestSteps.*;

import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import com.depromeet.breadmapbackend.domain.admin.post.domain.dto.command.EventCommand;
import com.depromeet.breadmapbackend.domain.admin.post.domain.dto.command.UpdateEventOrderCommand;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.utils.ControllerTest;

/**
 * PostAdminControllerTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/24
 */
@DisplayName("Post(커뮤니티)Admin controller 테스트")
class PostAdminControllerTest extends ControllerTest {

	private String 관리자_토큰;

	@Autowired
	private DataSource dataSource;

	@BeforeEach
	void setUp() throws Exception {
		setUpTestDate();
		관리자_토큰 = jwtTokenProvider.createJwtToken("admin@email.com", RoleType.ADMIN.getCode()).getAccessToken();
	}

	@Test
	void 이벤트_조회() throws Exception {
		// given
		// when
		final var 결과 = 이벤트_조회_요청(관리자_토큰, mockMvc);
		// then
		이벤트_조회_요청_결과_검증(결과);
	}

	@Test
	void 이벤트_등록() throws Exception {
		// given
		final var 이벤트_등록_요청_데이터 = new EventCommand(false, false, false, "titleeeeeeeeeeee", "contenttttttttttttttt",
			"bannerImageeeeee", null);
		// when
		final var 결과 = 이벤트_등록_요청(이벤트_등록_요청_데이터, 관리자_토큰, mockMvc, objectMapper);
		// then
		이벤트_등록_요청_결과_검증(결과);
	}

	@Test
	void 이벤트_고정_가능여부_조회() throws Exception {
		// given
		// when
		final var 결과 = 이벤트_고정_가능여부_조회_요청(관리자_토큰, mockMvc);
		// then
		이벤트_고정_가능여부_조회_결과_검증(결과);
	}

	@Test
	void 이벤트_수정() throws Exception {
		// given
		final var 이벤트_수정_요청_데이터 = new EventCommand(false, false, false, "titleeeeeeeeeeee", "contenttttttttttttttt",
			"bannerImageeeeee", null);

		// when
		final var 결과 = 이벤트_수정_요청(이벤트_수정_요청_데이터, 116L, 관리자_토큰, mockMvc, objectMapper);
		// then
		이벤트_수정_결과_검증(결과);
	}

	@Test
	void 이벤트_정렬_순서_수정() throws Exception {
		// given
		final var 이벤트_순서_수정_요청_데이터 = List.of(
			new UpdateEventOrderCommand(16, 112L),
			new UpdateEventOrderCommand(1, 126L),
			new UpdateEventOrderCommand(2, 113L)
		);

		// when
		final var 결과 = 이벤트_순서_수정_요청(이벤트_순서_수정_요청_데이터, 관리자_토큰, mockMvc, objectMapper);
		// then
		이벤트_순서_수정_결과_검증(결과);
	}

	@Test
	void 이벤트_캐러셀_조회() throws Exception {
		// given
		// when
		final var 결과 = 이벤트_캐러셀_조회_요청(관리자_토큰, mockMvc);
		// then
		이벤트_캐러셀_조회_결과_검증(결과);
	}

	@Test
	void 이벤트_상세_조회() throws Exception {
		// given
		final var 이벤트_조회_고유번호 = 112L;
		// when
		final var 결과 = 이벤트_상세_조회_요청(이벤트_조회_고유번호, 관리자_토큰, mockMvc);
		// then
		이벤트_상세_조회_결과_검증(결과);
	}

	private void setUpTestDate() throws Exception {
		try (final Connection connection = dataSource.getConnection()) {
			ScriptUtils.executeSqlScript(connection, new ClassPathResource("post-admin-test-data.sql"));
		}
	}

}