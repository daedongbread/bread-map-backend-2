package com.depromeet.breadmapbackend.domain.admin.search;

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

import com.depromeet.breadmapbackend.domain.admin.search.dto.HotKeywordUpdateRequest;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.utils.ControllerTest;

/**
 * AdminHotKeywordControllerTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 11/10/23
 */
@DisplayName("AdminHotKeywordController(어드민 검색어 랭킹) controller 테스트")
class AdminHotKeywordControllerTest extends ControllerTest {

	private String userToken;
	private static final String BASE_URL = "/v1/admin/search/hot-keywords";

	@Autowired
	private DataSource dataSource;

	@BeforeEach
	void setUp() throws Exception {
		setUpTestDate();
		userToken = jwtTokenProvider.createJwtToken("admin@email.com", RoleType.ADMIN.getCode()).getAccessToken();
	}

	private void setUpTestDate() throws Exception {
		try (final Connection connection = dataSource.getConnection()) {
			ScriptUtils.executeSqlScript(connection, new ClassPathResource("hot-keyword-test-data.sql"));

		}
	}

	@Test
	void 랭킹_조회_요청_시_기대하는_응답을_반환한다() throws Exception {
		// given

		// when
		final var result = mockMvc.perform(get(BASE_URL + "/rank")
				.header("Authorization", "Bearer " + userToken))
			.andDo(print())
			.andDo(document("v1/admin/search/hot-keywords/rank",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
				responseFields(
					fieldWithPath("data.[].keyword").description("인기 검색어"),
					fieldWithPath("data.[].rank").description("순위")
				)
			));

		//then
		result.andExpect(status().isOk());
	}

	@Test
	void 검색어_검색_횟수_조회_요청_시_기대하는_응답을_반환한다() throws Exception {
		// given
		final String sortType = "ONE_MONTH";

		// when
		final var result = mockMvc.perform(get(BASE_URL)
				.header("Authorization", "Bearer " + userToken)
				.param("sortType", sortType))
			.andDo(print())
			.andDo(document("v1/admin/search/hot-keywords",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
				requestParameters(
					parameterWithName("sortType").description("정렬 조건 (ONE_WEEK, ONE_MONTH, THREE_MONTH)")),
				responseFields(
					fieldWithPath("data.[].id").description("인기 검색어 id"),
					fieldWithPath("data.[].keyword").description("인기 검색어"),
					fieldWithPath("data.[].oneWeekCount").description("일주일 검색량"),
					fieldWithPath("data.[].oneMonthCount").description("1개월 검색량"),
					fieldWithPath("data.[].threeMonthCount").description("3개월 검색량")
				)
			));
		// then
		result.andExpect(status().isOk());
	}

	@Test
	void 랭킹_업데이트_시_기대하는_응답을_반환한다() throws Exception {
		// given
		final List<HotKeywordUpdateRequest.HotKeywordInfo> request = List.of(
			new HotKeywordUpdateRequest.HotKeywordInfo(
				"대동빵", 2
			),
			new HotKeywordUpdateRequest.HotKeywordInfo(
				"소금빵", 1
			),
			new HotKeywordUpdateRequest.HotKeywordInfo(
				"강남역", 3
			)

		);

		final String requestInString = objectMapper.writeValueAsString(new HotKeywordUpdateRequest(request));

		// when
		final var result = mockMvc.perform(put(BASE_URL + "/rank")
				.content(requestInString).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + userToken))
			.andDo(print())
			.andDo(document("v1/admin/search/hot-keywords/rank/update",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
				requestFields(
					fieldWithPath("HotKeywordList").description("인기 검색어 랭킹 변경 리스트"),
					fieldWithPath("HotKeywordList.[].keyword").description("검색어"),
					fieldWithPath("HotKeywordList.[].rank").description("랭킹")
				)
			));

		// then
		result.andExpect(status().isAccepted());
	}

}