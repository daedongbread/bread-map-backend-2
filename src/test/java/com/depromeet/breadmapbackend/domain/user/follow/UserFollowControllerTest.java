package com.depromeet.breadmapbackend.domain.user.follow;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.depromeet.breadmapbackend.domain.user.OAuthInfo;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserInfo;
import com.depromeet.breadmapbackend.domain.user.follow.dto.FollowRequest;
import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.utils.ControllerTest;

class UserFollowControllerTest extends ControllerTest {
	private User user1;
	private User user2;
	private JwtToken token1;
	private JwtToken token2;

	@BeforeEach
	public void setUp() {
		user1 = User.builder().oAuthInfo(OAuthInfo.builder().oAuthType(OAuthType.GOOGLE).oAuthId("oAuthId1").build())
			.userInfo(UserInfo.builder().nickName("nickname1").build()).build();
		user2 = User.builder().oAuthInfo(OAuthInfo.builder().oAuthType(OAuthType.GOOGLE).oAuthId("oAuthId2").build())
			.userInfo(UserInfo.builder().nickName("nickname2").build()).build();
		userRepository.save(user1);
		userRepository.save(user2);

		token1 = jwtTokenProvider.createJwtToken(user1.getOAuthId(), RoleType.USER.getCode());
		token2 = jwtTokenProvider.createJwtToken(user2.getOAuthId(), RoleType.USER.getCode());

		Follow follow1 = Follow.builder().fromUser(user1).toUser(user2).build();
		Follow follow2 = Follow.builder().fromUser(user2).toUser(user1).build();
		followRepository.save(follow1);
		followRepository.save(follow2);
	}

	@AfterEach
	public void setDown() {
		noticeTokenRepository.deleteAllInBatch();
		noticeRepository.deleteAllInBatch();
		followRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
	}

	@Test
		//    @Transactional
	void follow() throws Exception {
		followRepository.deleteAllInBatch();
		String object = objectMapper.writeValueAsString(FollowRequest.builder().userId(user1.getId()).build());

		mockMvc.perform(post("/v1/users/follow")
				.content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token2.getAccessToken()))
			.andDo(print())
			.andDo(document("v1/user/follow",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
				requestFields(fieldWithPath("userId").description("팔로우할 유저 고유번호"))
			))
			.andExpect(status().isCreated());
	}

	@Test
		//    @Transactional
	void unfollow() throws Exception {
		String object = objectMapper.writeValueAsString(FollowRequest.builder().userId(user2.getId()).build());

		mockMvc.perform(delete("/v1/users/follow")
				.content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token1.getAccessToken()))
			.andDo(print())
			.andDo(document("v1/user/unfollow",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
				requestFields(fieldWithPath("userId").description("언팔로우한 유저 고유번호"))
			))
			.andExpect(status().isNoContent());
	}

	@Test
		//    @Transactional
	void followerList() throws Exception {
		mockMvc.perform(get("/v1/users/{userId}/follower", user1.getId())
				.header("Authorization", "Bearer " + token2.getAccessToken()))
			.andDo(print())
			.andDo(document("v1/user/follower",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
				responseFields(
					fieldWithPath("data.[].userId").description("팔로워 유저 고유번호"),
					fieldWithPath("data.[].userImage").description("팔로워 유저 이미지"),
					fieldWithPath("data.[].nickName").description("팔로워 유저 닉네임"),
					fieldWithPath("data.[].reviewNum").description("팔로워 유저 리뷰 수"),
					fieldWithPath("data.[].followerNum").description("팔로워 유저 팔로워 수"),
					fieldWithPath("data.[].isFollow").description("팔로워 유저 팔로우 여부"),
					fieldWithPath("data.[].isMe").description("팔로워 유저 본인 여부")
				)
			))
			.andExpect(status().isOk());
	}

	@Test
		//    @Transactional
	void followingList() throws Exception {
		mockMvc.perform(get("/v1/users/{userId}/following", user1.getId())
				.header("Authorization", "Bearer " + token2.getAccessToken()))
			.andDo(print())
			.andDo(document("v1/user/following",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
				responseFields(
					fieldWithPath("data.[].userId").description("팔로잉 유저 고유번호"),
					fieldWithPath("data.[].userImage").description("팔로잉 유저 이미지"),
					fieldWithPath("data.[].nickName").description("팔로잉 유저 닉네임"),
					fieldWithPath("data.[].reviewNum").description("팔로잉 유저 리뷰 수"),
					fieldWithPath("data.[].followerNum").description("팔로잉 유저 팔로워 수"),
					fieldWithPath("data.[].isFollow").description("팔로잉 유저 팔로우 여부"),
					fieldWithPath("data.[].isMe").description("팔로잉 유저 본인 여부")
				)
			))
			.andExpect(status().isOk());
	}
}
