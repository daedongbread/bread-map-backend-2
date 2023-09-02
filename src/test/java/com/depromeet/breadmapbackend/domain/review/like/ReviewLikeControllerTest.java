package com.depromeet.breadmapbackend.domain.review.like;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
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

class ReviewLikeControllerTest extends ControllerTest {
	private JwtToken token;
	private Review review1;
	private Review review2;

	@BeforeEach
	public void setup() {
		List<String> images = List.of("test images");
		User user = User.builder()
			.oAuthInfo(OAuthInfo.builder().oAuthType(OAuthType.GOOGLE).oAuthId("oAuthId1").build())
			.userInfo(UserInfo.builder().nickName("nickname1").build())
			.build();
		userRepository.save(user);
		token = jwtTokenProvider.createJwtToken(user.getOAuthId(), user.getRoleType().getCode());

		List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);
		Bakery bakery = Bakery.builder()
			.address("address")
			.latitude(37.5596080725671)
			.longitude(127.044235133983)
			.images(new ArrayList<>(images))
			.facilityInfoList(facilityInfo)
			.name("bakery1")
			.status(BakeryStatus.POSTING)
			.build();
		bakeryRepository.save(bakery);

		review1 = Review.builder().user(user).bakery(bakery).content("content1").build();
		reviewRepository.save(review1);

		review2 = Review.builder().user(user).bakery(bakery).content("content2").build();
		reviewRepository.save(review2);

		ReviewLike reviewLike = ReviewLike.builder().review(review2).user(user).build();
		reviewLikeRepository.save(reviewLike);
	}

	@AfterEach
	public void setDown() {
		reviewLikeRepository.deleteAllInBatch();
		reviewRepository.deleteAllInBatch();
		bakeryRepository.deleteAllInBatch();
		noticeRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
	}

	@Test
		//    @Transactional
	void reviewLike() throws Exception {
		mockMvc.perform(post("/v1/reviews/{reviewId}/like", review1.getId())
				.header("Authorization", "Bearer " + token.getAccessToken()))
			.andDo(print())
			.andDo(document("v1/review/like",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
				pathParameters(parameterWithName("reviewId").description("리뷰 고유 번호"))
			))
			.andExpect(status().isCreated());
	}

	@Test
		//    @Transactional
	void reviewUnlike() throws Exception {
		mockMvc.perform(delete("/v1/reviews/{reviewId}/unlike", review2.getId())
				.header("Authorization", "Bearer " + token.getAccessToken()))
			.andDo(print())
			.andDo(document("v1/review/unlike",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
				pathParameters(parameterWithName("reviewId").description("리뷰 고유 번호"))
			))
			.andExpect(status().isNoContent());
	}
}
