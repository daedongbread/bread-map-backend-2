package com.depromeet.breadmapbackend.domain.search;

import com.depromeet.breadmapbackend.domain.search.dto.SearchResultDto;
import com.depromeet.breadmapbackend.domain.search.dto.SearchType;
import com.depromeet.breadmapbackend.domain.search.dto.keyword.response.SearchResultResponse;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.utils.ControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

class SearchV2ControllerTest extends ControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DataSource dataSource;
    @MockBean
    private SearchService searchService;
    private String userToken;

    private void setUpTestDate() throws Exception {
        try (final Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("user-test-data.sql"));
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
        List<SearchResultDto> searchResultDtoList = new ArrayList<>();
        SearchResultDto searchResultDto = SearchResultDto.builder()
                .bakeryId(1L)
                .bakeryName("Test Bakery")
                .breadId(1L)
                .breadName("Test Bread")
                .address("Test Address")
                .totalScore(5d)
                .reviewNum(5L)
                .distance(100d)
                .build();

        searchResultDtoList.add(searchResultDto);

        SearchResultResponse searchResultResponseMock = SearchResultResponse
                .builder()
                .subwayStationName("역삼역")
                .searchResultDtoList(searchResultDtoList)
                .build();

        when(searchService.searchEngine(eq("TEST_111"), eq(keyword), eq(latitude), eq(longitude), eq(searchType)))
                .thenReturn(searchResultResponseMock);

        mockMvc.perform(MockMvcRequestBuilders.get("/v2/search/keyword")
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
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
