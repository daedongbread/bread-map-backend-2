package com.depromeet.breadmapbackend.domain.search;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.user.OAuthInfo;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserInfo;
import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.utils.ControllerTest;

class SearchControllerTest extends ControllerTest {
	private JwtToken token;

	@BeforeEach
	public void setup() {
		List<String> images = List.of("test images");
		User user = User.builder()
			.oAuthInfo(OAuthInfo.builder().oAuthType(OAuthType.GOOGLE).oAuthId("oAuthId1").build())
			.userInfo(UserInfo.builder().nickName("nickname1").build())
			.build();
		userRepository.save(user);
		token = jwtTokenProvider.createJwtToken(user.getOAuthId(), user.getRoleType().getCode());

		Bakery bakery1 = Bakery.builder()
			.address("address")
			.latitude(37.5596080725671)
			.longitude(127.044235133983)
			.images(new ArrayList<>(images))
			.facilityInfoList(Collections.singletonList(FacilityInfo.PARKING))
			.name("bakery1")
			.status(BakeryStatus.POSTING)
			.build();
		Bakery bakery2 = Bakery.builder()
			.address("address")
			.latitude(37.55950448505721)
			.longitude(127.04416263787213)
			.images(new ArrayList<>(images))
			.facilityInfoList(Collections.singletonList(FacilityInfo.DELIVERY))
			.name("bakery2")
			.status(BakeryStatus.POSTING)
			.build();
		bakeryRepository.save(bakery1);
		bakeryRepository.save(bakery2);

		Review review1 = Review.builder().user(user).bakery(bakery1).content("content1").build();
		Review review2 = Review.builder().user(user).bakery(bakery2).content("content1").build();
		reviewRepository.save(review1);
		reviewRepository.save(review2);
	}

	@AfterEach
	public void setDown() {
		reviewRepository.deleteAllInBatch();
		bakeryRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
	}

	//    @Test
	//    void autoComplete() throws Exception {
	//        mockMvc.perform(get("/v1/search/auto?word=ba&latitude=37.560992&longitude=127.044174")
	//                .header("Authorization", "Bearer " + token.getAccessToken()))
	//                .andDo(print())
	//                .andDo(document("v1/search/auto",
	//                        preprocessRequest(prettyPrint()),
	//                        preprocessResponse(prettyPrint()),
	//                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
	//                        requestParameters(
	//                                parameterWithName("word").description("검색어"),
	//                                parameterWithName("latitude").description("중앙 위도"),
	//                                parameterWithName("longitude").description("중앙 경도")
	//                        ),
	//                        responseFields(
	//                                fieldWithPath("data.[].bakeryId").description("빵집 고유 번호"),
	//                                fieldWithPath("data.[].bakeryName").description("빵집 이름"),
	//                                fieldWithPath("data.[].reviewNum").description("빵집 리뷰 갯수"),
	//                                fieldWithPath("data.[].distance").description("빵집까지 거리")
	//                        )
	//                ))
	//                .andExpect(status().isOk());
	//    }

	@Test
	void search() throws Exception {
		mockMvc.perform(get("/v1/search?word=bakery1&latitude=37.560992&longitude=127.044174")
				.header("Authorization", "Bearer " + token.getAccessToken()))
			.andDo(print())
			.andDo(document("v1/search/search",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
				requestParameters(
					parameterWithName("word").description("검색어"),
					parameterWithName("latitude").description("중앙 위도"),
					parameterWithName("longitude").description("중앙 경도")
				),
				responseFields(
					fieldWithPath("data.[].bakeryId").description("빵집 고유 번호"),
					fieldWithPath("data.[].bakeryName").description("빵집 이름"),
					fieldWithPath("data.[].address").description("빵집 주소"),
					fieldWithPath("data.[].rating").description("빵집 점수"),
					fieldWithPath("data.[].reviewNum").description("빵집 리뷰 갯수"),
					fieldWithPath("data.[].distance").description("빵집까지 거리")
				)
			))
			.andExpect(status().isOk());
	}

	//    @Test
	//    void recentKeywords() throws Exception {
	//        mockMvc.perform(get("/v1/search/keywords")
	//                .header("Authorization", "Bearer " + token.getAccessToken()))
	//                .andDo(print())
	//                .andDo(document("v1/search/keywords",
	//                        preprocessRequest(prettyPrint()),
	//                        preprocessResponse(prettyPrint()),
	//                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
	//                        responseFields(
	//                                fieldWithPath("data").description("최근 검색어")
	//                        )
	//                ))
	//                .andExpect(status().isOk());
	//    }
	//
	//    @Test
	//    void deleteRecentKeyword() throws Exception {
	//        mockMvc.perform(delete("/v1/search/keywords?keyword=bakery")
	//                .header("Authorization", "Bearer " + token.getAccessToken()))
	//                .andDo(print())
	//                .andDo(document("v1/search/keywords/delete",
	//                        preprocessRequest(prettyPrint()),
	//                        preprocessResponse(prettyPrint()),
	//                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
	//                        requestParameters(
	//                                parameterWithName("keyword").description("삭제 키워드")
	//                        )
	//                ))
	//                .andExpect(status().isNoContent());
	//    }
	//
	//    @Test
	//    void deleteRecentKeywordAll() throws Exception {
	//        mockMvc.perform(delete("/v1/search/keywords/all")
	//                .header("Authorization", "Bearer " + token.getAccessToken()))
	//                .andDo(print())
	//                .andDo(document("v1/search/keywords/deleteAll",
	//                        preprocessRequest(prettyPrint()),
	//                        preprocessResponse(prettyPrint()),
	//                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token"))
	//                ))
	//                .andExpect(status().isNoContent());
	//    }
}