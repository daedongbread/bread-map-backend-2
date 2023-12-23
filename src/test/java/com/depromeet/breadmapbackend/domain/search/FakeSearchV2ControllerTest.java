package com.depromeet.breadmapbackend.domain.search;

import com.depromeet.breadmapbackend.domain.search.dto.SearchType;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.utils.ControllerTest;
import com.depromeet.breadmapbackend.utils.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestConfig.class)
class FakeSearchV2ControllerTest extends ControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private DataSource dataSource;

	private String userToken;

	private void setUpTestDate() throws Exception {
		try (final Connection connection = dataSource.getConnection()) {
			ScriptUtils.executeSqlScript(connection, new ClassPathResource("user-test-data.sql"));
			ScriptUtils.executeSqlScript(connection, new ClassPathResource("hot-keyword-test-data.sql"));
		}
	}

	@BeforeEach
	void setUp() throws Exception {
		setUpTestDate();
		userToken = jwtTokenProvider.createJwtToken("TEST_111", RoleType.USER.getCode()).getAccessToken();
	}

	@Test
	void searchKeyword() throws Exception {
		String keyword = "베이커리";
		double latitude = 127.34d;
		double longitude = 36.78d;
		SearchType searchType = SearchType.POPULAR;

		// Then

		mockMvc.perform(get("/v2/search/keyword")
				.header("Authorization", "Bearer " + userToken)
				.param("oAuthId", "TEST_111")
				.param("keyword", keyword)
				.param("latitude", String.valueOf(latitude))
				.param("longitude", String.valueOf(longitude))
				.param("searchType", searchType.toString())
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andDo(document("v2/search/keyword",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestParameters(
					parameterWithName("oAuthId").description("Current User"),
					parameterWithName("keyword").description("검색 키워드"),
					parameterWithName("latitude").description("중앙 위도"),
					parameterWithName("longitude").description("중앙 경도"),
					parameterWithName("searchType").description("검색 타입")
				),
				responseFields(
					fieldWithPath("data.subwayStationName").description("지하철역 명"),
					fieldWithPath("data.searchResultDtoList.[].bakeryId").description("빵집 ID"),
					fieldWithPath("data.searchResultDtoList.[].bakeryName").description("빵집 이름"),
					fieldWithPath("data.searchResultDtoList.[].breadId").description("빵 ID"),
					fieldWithPath("data.searchResultDtoList.[].breadName").description("빵 이름"),
					fieldWithPath("data.searchResultDtoList.[].address").description("빵집 주소"),
					fieldWithPath("data.searchResultDtoList.[].totalScore").description("빵집 점수"),
					fieldWithPath("data.searchResultDtoList.[].reviewNum").description("빵집 리뷰 갯수"),
					fieldWithPath("data.searchResultDtoList.[].distance").description("빵집까지 거리"),
					fieldWithPath("data.searchResultDtoList.[].latitude").description("위도"),
					fieldWithPath("data.searchResultDtoList.[].longitude").description("경도"),
					fieldWithPath("data.searchResultDtoList.[].bakeryImageUrl").description("빵집 이미지 URL"),
					fieldWithPath("data.searchResultDtoList.[].flagCount").description("깃발 갯수"),
					fieldWithPath("data.searchResultDtoList.[].flagColor").description("깃발 색깔")
				)
			))
			.andExpect(status().isOk());
	}

	@Test
	void searchKeywordSuggestions() throws Exception {
		String keyword = "베이커리";

		mockMvc.perform(get("/v2/search/suggestions")
				.header("Authorization", "Bearer " + userToken)
				.param("keyword", keyword)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andDo(document("v2/search/suggestions",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestParameters(
					parameterWithName("keyword").description("검색 키워드")
				),
				responseFields(
					fieldWithPath("data.keywordSuggestions").description("추천 검색어 리스트")
				)
			))
			.andExpect(status().isOk());
	}
}
