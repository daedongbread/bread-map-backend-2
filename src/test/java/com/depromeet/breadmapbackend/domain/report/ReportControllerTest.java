package com.depromeet.breadmapbackend.domain.report;

import static com.depromeet.breadmapbackend.domain.report.ReportControllerAssertions.*;
import static com.depromeet.breadmapbackend.domain.report.ReportControllerTestSteps.*;
import static com.depromeet.breadmapbackend.domain.report.ReportReason.*;

import java.sql.Connection;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import com.depromeet.breadmapbackend.domain.report.dto.request.ReportRequest;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.utils.ControllerTest;

/**
 * ReportControllerTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/26
 */

@DisplayName("Post(커뮤니티) controller 테스트")
class ReportControllerTest extends ControllerTest {
	private String 사용자_토큰;

	@Autowired
	private DataSource dataSource;

	@BeforeEach
	void setUp() throws Exception {
		setUpTestDate();
		사용자_토큰 = jwtTokenProvider.createJwtToken("APPLE_111", RoleType.USER.getCode()).getAccessToken();
	}

	@Test
	void 커뮤니티_신고() throws Exception {
		// given
		final var 신고_요청_데이터 = new ReportRequest(IRRELEVANT_CONTENT, null);
		final var 신고_대상_커뮤니티_타입 = "BREAD_STORY";
		final var 신고_대상_커뮤니티_고유_번호 = 222;

		// when
		final var 결과 = 커뮤니티_신고_요청(신고_요청_데이터, 신고_대상_커뮤니티_타입, 신고_대상_커뮤니티_고유_번호, 사용자_토큰, mockMvc, objectMapper);

		// then
		커뮤니티_신고_요청_결과_검증(결과);

	}

	private void setUpTestDate() throws Exception {
		try (final Connection connection = dataSource.getConnection()) {
			ScriptUtils.executeSqlScript(connection, new ClassPathResource("report-test-data.sql"));
		}
	}

}