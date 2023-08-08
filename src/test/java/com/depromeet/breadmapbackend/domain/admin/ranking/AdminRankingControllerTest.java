package com.depromeet.breadmapbackend.domain.admin.ranking;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.ResultActions;

import com.depromeet.breadmapbackend.domain.admin.ranking.dto.RankingUpdateRequest;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.utils.ControllerTest;

/**
 * AdminRankingControllerTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/19
 */

@DisplayName("AdminRankingControllerTest(어드민 빵집 랭킹) controller 테스트")
public class AdminRankingControllerTest extends ControllerTest {

	private String 사용자_토큰;

	@Autowired
	private DataSource dataSource;

	@BeforeEach
	void setUp() throws Exception {
		setUpTestDate();
		사용자_토큰 = jwtTokenProvider.createJwtToken("admin@email.com", RoleType.ADMIN.getCode()).getAccessToken();
	}

	@Test
	void 랭킹_조회_요청_시_기대하는_응답을_반환한다() throws Exception {
		// given
		final String 조회_시작일자 = "2023-07-07";

		// when
		final var 결과 = 랭킹_조회_요청(조회_시작일자, 사용자_토큰);

		// then
		랭킹_조회_요청_결과_검증(결과);
	}

	@Test
	void 랭킹_업데이트_시_기대하는_응답을_반환한다() throws Exception {
		// given
		final List<RankingUpdateRequest.BakeryRankInfo> bakeryRankInfos = List.of(
			new RankingUpdateRequest.BakeryRankInfo(
				111L, 2
			),
			new RankingUpdateRequest.BakeryRankInfo(
				115L, 1
			),
			new RankingUpdateRequest.BakeryRankInfo(
				112L, 5
			)
		);
		final String 요청 = objectMapper.writeValueAsString(new RankingUpdateRequest(bakeryRankInfos));
		// when
		final var 결과 = 랭킹_수정_요청(요청, 사용자_토큰);

		// then
		랭킹_수정_요청_결과_검증(결과);
	}

	private void 랭킹_조회_요청_결과_검증(final ResultActions 결과) throws Exception {
		결과.andExpect(status().isOk());
	}

	private void 랭킹_수정_요청_결과_검증(final ResultActions 결과) throws Exception {
		결과.andExpect(status().isOk());
	}

	private ResultActions 랭킹_조회_요청(final String 조회_시작일자, final String 사용자_토큰) throws Exception {
		return mockMvc.perform(get("/v1/admin/rank/{startDate}", 조회_시작일자)
				.header("Authorization", "Bearer " + 사용자_토큰))
			.andDo(print())
			.andDo(document("v1/admin/bakery/rank",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
				pathParameters(parameterWithName("startDate").description("랭킹 조회 시작일자")),
				responseFields(
					fieldWithPath("data.startDate").description("조회 결과 시작일자"),
					fieldWithPath("data.endDate").description("조회 결과 종료일자"),
					fieldWithPath("data.dateList").description("조회 결과 일자 리스트"),
					fieldWithPath("data.simpleBakeryInfoList.[].id").description("랭킹 데이터 id ( 랭킹 수정 요청시 필요 )"),
					fieldWithPath("data.simpleBakeryInfoList.[].rank").description("빵집 랭킹"),
					fieldWithPath("data.simpleBakeryInfoList.[].bakeryId").description("빵집 고유 번호"),
					fieldWithPath("data.simpleBakeryInfoList.[].bakeryName").description("빵집 이름"),
					fieldWithPath("data.simpleBakeryInfoList.[].address").description("빵집 주소"),
					fieldWithPath("data.simpleBakeryInfoList.[].viewCount").description("빵집 조회수"),
					fieldWithPath("data.simpleBakeryInfoList.[].flagCount").description("빵집 깃발 추가 건수"),
					fieldWithPath("data.simpleBakeryInfoList.[].score").description("빵집 총 점수"),
					fieldWithPath("data.simpleBakeryInfoList.[].calculatedDate").description("빵집 점수 계산 일")
				)
			));

	}

	private ResultActions 랭킹_수정_요청(final String 요청, final String 사용자_토큰) throws Exception {
		return mockMvc.perform(post("/v1/admin/rank")
				.content(요청).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + 사용자_토큰))
			.andDo(print())
			.andDo(document("v1/admin/bakery/rank/update",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
				requestFields(
					fieldWithPath("bakeryRankInfoList").description("빵집 변경 리스트"),
					fieldWithPath("bakeryRankInfoList.[].id").description("랭킹 고유 번호"),
					fieldWithPath("bakeryRankInfoList.[].rank").description("랭킹")
				)
			));

	}

	private void setUpTestDate() throws Exception {
		try (final Connection connection = dataSource.getConnection()) {
			ScriptUtils.executeSqlScript(connection, new ClassPathResource("admin-rank-test-data.sql"));

		}
	}

}
