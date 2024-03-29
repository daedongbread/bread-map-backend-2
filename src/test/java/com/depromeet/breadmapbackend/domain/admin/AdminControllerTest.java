package com.depromeet.breadmapbackend.domain.admin;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

import com.depromeet.breadmapbackend.domain.admin.bakery.dto.AdminLoginRequest;
import com.depromeet.breadmapbackend.domain.auth.dto.ReissueRequest;
import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.report.ReviewReport;
import com.depromeet.breadmapbackend.domain.review.report.ReviewReportReason;
import com.depromeet.breadmapbackend.domain.user.OAuthInfo;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserInfo;
import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.utils.ControllerTest;

class AdminControllerTest extends ControllerTest {
	private JwtToken token;

	@BeforeEach
	public void setup() throws IOException {
		Admin admin = Admin.builder().email("Deadong01").password(passwordEncoder.encode("password")).build();
		adminRepository.save(admin);
		token = jwtTokenProvider.createJwtToken(admin.getEmail(), admin.getRoleType().getCode());
		redisTokenUtils.setRefreshToken(
			token.getRefreshToken(),
			admin.getEmail() + ":" + token.getAccessToken(),
			jwtTokenProvider.getRefreshTokenExpiredDate());

		User user = User.builder()
			.oAuthInfo(OAuthInfo.builder().oAuthType(OAuthType.GOOGLE).oAuthId("oAuthId1").build())
			.userInfo(UserInfo.builder().nickName("nickname1").build())
			.build();
		userRepository.save(user);

		List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);
		Bakery bakery = Bakery.builder().address("address").latitude(37.5596080725671).longitude(127.044235133983)
			.facilityInfoList(facilityInfo).name("bakery").status(BakeryStatus.POSTING)
			.images(List.of(
				customAWSS3Properties.getCloudFront() + "/" + "bakeryImage1.jpg",
				customAWSS3Properties.getCloudFront() + "/" + "bakeryImage2.jpg"
			)).build();
		bakeryRepository.save(bakery);

		s3Uploader.upload(
			new MockMultipartFile("image", "bakeryImage.jpg", "image/jpg", "test".getBytes()),
			"bakeryImage.jpg");

		Review review = Review.builder().user(user).bakery(bakery).content("content1").build();
		reviewRepository.save(review);

		ReviewReport reviewReport = ReviewReport.builder()
			.reporter(user).review(review).reason(ReviewReportReason.COPYRIGHT_THEFT).content("content").build();
		reviewReportRepository.save(reviewReport);
	}

	@AfterEach
	public void setDown() {
		redisTemplate.delete(token.getAccessToken());
		s3Uploader.deleteFileS3(customAWSS3Properties.getBucket() + "/bakeryImage.jpg");
		reviewReportRepository.deleteAllInBatch();
		reviewRepository.deleteAllInBatch();
		productRepository.deleteAllInBatch();
		bakeryRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
		adminRepository.deleteAllInBatch();
	}

	@Test
	void adminLogin() throws Exception {
		String object = objectMapper.writeValueAsString(AdminLoginRequest.builder()
			.email("Deadong01").password("password").build());

		ResultActions result = mockMvc.perform(post("/v1/admin/login")
			.content(object)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON));

		result.andDo(print())
			.andDo(document("v1/admin/login",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("email").description("관리자 이메일"),
					fieldWithPath("password").description("관리자 비밀번호")
				),
				responseFields(
					fieldWithPath("data.userId").description("유저 고유 번호"),
					fieldWithPath("data.accessToken").description("엑세스 토큰"),
					fieldWithPath("data.refreshToken").description("리프레시 토큰"),
					fieldWithPath("data.accessTokenExpiredDate").description("엑세스 토큰 만료시간")
				)
			))
			.andExpect(status().isCreated());
	}

	@Test
		//    @Transactional
	void reissue() throws Exception {
		// given
		String object = objectMapper.writeValueAsString(ReissueRequest.builder()
			.accessToken(token.getAccessToken()).refreshToken(token.getRefreshToken()).build());

		// when
		ResultActions result = mockMvc.perform(post("/v1/admin/reissue")
			.content(object)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON));

		// then
		result
			.andDo(print())
			.andDo(document("v1/admin/reissue",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("accessToken").description("엑세스 토큰"),
					fieldWithPath("refreshToken").description("리프레시 토큰"),
					fieldWithPath("deviceToken").description("알림 전송용 디바이스 토큰")
				),
				responseFields(
					fieldWithPath("data.userId").description("유저 고유 번호"),
					fieldWithPath("data.accessToken").description("엑세스 토큰"),
					fieldWithPath("data.refreshToken").description("리프레시 토큰"),
					fieldWithPath("data.accessTokenExpiredDate").description("엑세스 토큰 만료시간")
				)
			))
			.andExpect(status().isCreated());
	}

	@Test
	void getAdminBar() throws Exception {
		mockMvc.perform(get("/v1/admin/bar")
				.header("Authorization", "Bearer " + token.getAccessToken()))
			.andDo(print())
			.andDo(document("v1/admin/bar",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
				responseFields(
					fieldWithPath("data.bakeryAddReportCount").description("`검토전`인 빵집 추가 제보 수"),
					fieldWithPath("data.bakeryCount").description("`미게시`인 빵집 수"),
					fieldWithPath("data.reviewReportCount").description("숨김 처리하지 않은 리뷰 신고 수")
				)
			))
			.andExpect(status().isOk());
	}

	@Test
	void uploadImage() throws Exception {
		mockMvc.perform(multipart("/v1/admin/images")
				.file(new MockMultipartFile("image", UUID.randomUUID() + ".png", "image/png", "test".getBytes()))
				.header("Authorization", "Bearer " + token.getAccessToken()))
			.andDo(print())
			.andDo(document("v1/admin/tempImage",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
				requestParts(
					partWithName("image").description("업로드 이미지")),
				responseFields(
					fieldWithPath("data.imagePath").description("업로드된 이미지"))
			))
			.andExpect(status().isCreated());
	}
}
