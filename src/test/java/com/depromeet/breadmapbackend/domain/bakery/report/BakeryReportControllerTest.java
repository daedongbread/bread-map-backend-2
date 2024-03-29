package com.depromeet.breadmapbackend.domain.bakery.report;

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
import org.springframework.http.MediaType;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.bakery.report.dto.BakeryAddReportRequest;
import com.depromeet.breadmapbackend.domain.bakery.report.dto.BakeryReportImageRequest;
import com.depromeet.breadmapbackend.domain.bakery.report.dto.BakeryUpdateReportRequest;
import com.depromeet.breadmapbackend.domain.user.OAuthInfo;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserInfo;
import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.utils.ControllerTest;

class BakeryReportControllerTest extends ControllerTest {
	private Bakery bakery;
	private JwtToken token;

	@BeforeEach
	public void setup() {
		User user = User.builder()
			.oAuthInfo(OAuthInfo.builder().oAuthType(OAuthType.GOOGLE).oAuthId("oAuthId1").build())
			.userInfo(UserInfo.builder().nickName("nickname1").build())
			.build();
		userRepository.save(user);
		token = jwtTokenProvider.createJwtToken(user.getOAuthId(), user.getRoleType().getCode());

		List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);
		bakery = Bakery.builder()
			.address("address1")
			.latitude(37.5596080725671)
			.longitude(127.044235133983)
			.images(new ArrayList<>(List.of("test images")))
			.facilityInfoList(facilityInfo)
			.name("bakery1")
			.status(BakeryStatus.POSTING)
			.build();
		bakeryRepository.save(bakery);
	}

	@AfterEach
	public void setDown() {
		bakeryUpdateReportImageRepository.deleteAllInBatch();
		bakeryUpdateReportRepository.deleteAllInBatch();
		bakeryAddReportImageRepository.deleteAllInBatch();
		bakeryAddReportRepository.deleteAllInBatch();
		bakeryReportImageRepository.deleteAllInBatch();
		bakeryRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
	}

	@Test
	void bakeryAddReport() throws Exception {
		String object = objectMapper.writeValueAsString(BakeryAddReportRequest.builder()
			.name("newBakery")
			.location("newLocation")
			.content("newContent")
			.images(List.of("image1", "image2"))
			.build());

		mockMvc.perform(post("/v1/bakeries/bakery-add-reports")
				.header("Authorization", "Bearer " + token.getAccessToken())
				.content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andDo(document("v1/bakery/report/add",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
				requestFields(
					fieldWithPath("name").description("제보 빵집 이름"),
					fieldWithPath("location").description("제보 빵집 위치"),
					fieldWithPath("content").optional().description("추천 이유"),
					fieldWithPath("images").optional().description("제보 빵집 이미지들")
				)
			))
			.andExpect(status().isCreated());
	}

	@Test
	void bakeryUpdateReport() throws Exception {
		String object = objectMapper.writeValueAsString(BakeryUpdateReportRequest.builder()
			.content("newContent").images(List.of("image1", "image2")).build());

		mockMvc.perform(post("/v1/bakeries/{bakeryId}/bakery-update-reports", bakery.getId())
				.header("Authorization", "Bearer " + token.getAccessToken())
				.content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andDo(document("v1/bakery/report/update",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
				pathParameters(parameterWithName("bakeryId").description("빵집 고유 번호")),
				requestFields(
					//                                fieldWithPath("reason").description("빵집 수정 제보 이유"),
					fieldWithPath("content").description("수정 사항"),
					fieldWithPath("images").optional().description("빵집 수정 제보 이미지들")
				)
			))
			.andExpect(status().isCreated());
	}

	@Test
	void bakeryReportImage() throws Exception {
		String object = objectMapper.writeValueAsString(BakeryReportImageRequest.builder()
			.images(List.of("image1", "image2")).build());

		mockMvc.perform(post("/v1/bakeries/{bakeryId}/bakery-report-images", bakery.getId())
				.header("Authorization", "Bearer " + token.getAccessToken())
				.content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andDo(document("v1/bakery/report/image",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
				pathParameters(parameterWithName("bakeryId").description("빵집 고유 번호")),
				requestFields(
					fieldWithPath("images").description("빵집 사진 제보 이미지들")
				)
			))
			.andExpect(status().isCreated());
	}
}
