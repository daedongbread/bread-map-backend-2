package com.depromeet.breadmapbackend.domain.bakery.ranking.controller;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.ResultActions;

import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.utils.ControllerTest;

/**
 * ScoredBakeryControllerTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/02
 */

@DisplayName("ScoredBakery(랭킹) controller 테스트")
public class ScoredBakeryControllerTest extends ControllerTest {

	private String 사용자_토큰;

	@Autowired
	private DataSource dataSource;

	@BeforeEach
	void setUp() throws Exception {
		setUpTestDate();
		사용자_토큰 = jwtTokenProvider.createJwtToken("APPLE_111", RoleType.USER.getCode()).getAccessToken();
	}

	@Test
	void 랭킹_조회_요청_시_기대하는_응답을_반환한다() throws Exception {
		// given
		final int 조회_건수 = 3;

		// when
		final var 결과 = 랭킹_조회_요청(조회_건수, 사용자_토큰);

		// then
		랭킹_조회_요청_결과_검증(결과);
	}

	private void 랭킹_조회_요청_결과_검증(final ResultActions 결과) throws Exception {

		결과.andExpect(status().isOk());

	}

	private ResultActions 랭킹_조회_요청(final int 조회_건수, final String 사용자_토큰) throws Exception {
		return mockMvc.perform(get("/v1/bakeries/rank/{count}", 조회_건수)
				.header("Authorization", "Bearer " + 사용자_토큰))
			.andDo(print())
			.andDo(document("v1/bakery/rank",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
				pathParameters(parameterWithName("count").description("랭킹 조회 count")),
				responseFields(
					fieldWithPath("data.[].id").description("빵집 고유번호"),
					fieldWithPath("data.[].name").description("빵집 이름"),
					fieldWithPath("data.[].image").description("빵집 사진"),
					fieldWithPath("data.[].flagNum").description("빵집 깃발 개수"),
					fieldWithPath("data.[].rating").description("빵집 평점"),
					fieldWithPath("data.[].shortAddress").description("빵집 축약 주소"),
					fieldWithPath("data.[].isFlagged").description("해당 유저 빵집 깃발 추가 여부")
				)
			));

	}

	private void setUpTestDate() throws Exception {
		try (final Connection connection = dataSource.getConnection()) {
			ScriptUtils.executeSqlScript(connection, new ClassPathResource("scoredBakery-test-data.sql"));
			final String sql = """
						insert into scored_bakery (id , flag_count, view_count, total_score,bakery_id, calculated_date, bakery_rank) values
						(100, 50, 50 ,  100, 100, '%s', 3),
						(101, 10, 10,   20, 200, '%s', 4),
						(102, 10, 5,    15, 300, '%s', 5),
						(103, 31, 1500, 1531, 500, '%s', 2),
						(104, 31, 1500, 1531, 600, '%s', 1),
						(105,  0, 0,       0, 700, '%s', 6)
				""".replaceAll("%s", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.executeUpdate();
		}
	}
}
