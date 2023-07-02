package com.depromeet.breadmapbackend.domain.bakery.ranking;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

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

	@BeforeEach
	void setUp() {
		사용자_토큰 = jwtTokenProvider.createJwtToken("APPLE_111", RoleType.USER.getCode()).getAccessToken();
	}

	@Test
	@Sql("classpath:scoredBakery-test-data.sql")
	void 랭킹_조회_요청_시_기대하는_응답을_반환한다() throws Exception {
		//given
		final int count = 5;

		mockMvc.perform(get("/v1/bakeries/rank/{count}", count)
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
			))
			.andExpect(status().isOk());

	}

}
