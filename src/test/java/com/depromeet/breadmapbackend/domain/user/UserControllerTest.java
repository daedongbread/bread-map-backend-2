package com.depromeet.breadmapbackend.domain.user;

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
import org.springframework.test.web.servlet.ResultActions;

import com.depromeet.breadmapbackend.domain.notice.token.NoticeToken;
import com.depromeet.breadmapbackend.domain.user.dto.NoticeTokenRequest;
import com.depromeet.breadmapbackend.domain.user.dto.UpdateNickNameRequest;
import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.utils.ControllerTest;

class UserControllerTest extends ControllerTest {
	private User user;
	private JwtToken token;
	private NoticeToken noticeToken;

	@BeforeEach
	public void setUp() {
		user = User.builder().oAuthInfo(OAuthInfo.builder().oAuthType(OAuthType.GOOGLE).oAuthId("oAuthId1").build())
			.userInfo(UserInfo.builder().nickName("nickname1").build()).build();
		userRepository.save(user);

		token = jwtTokenProvider.createJwtToken(user.getOAuthId(), RoleType.USER.getCode());
		redisTokenUtils.setRefreshToken(
			token.getRefreshToken(),
			user.getOAuthId() + ":" + token.getAccessToken(),
			jwtTokenProvider.getRefreshTokenExpiredDate());

		noticeToken = NoticeToken.builder().user(user).deviceToken("deviceToken1").build();
		noticeTokenRepository.save(noticeToken);
	}

	@AfterEach
	public void setDown() {
		redisTemplate.delete(token.getAccessToken());
		noticeTokenRepository.deleteAllInBatch();
		noticeRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
	}

	@Test
		//    @Transactional
	void profile() throws Exception {
		mockMvc.perform(get("/v1/users/{userId}", user.getId())
				.header("Authorization", "Bearer " + token.getAccessToken()))
			.andDo(print())
			.andDo(document("v1/user/profile",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
				responseFields(
					fieldWithPath("data.userId").description("유저 고유 번호"),
					fieldWithPath("data.userImage").description("유저 이미지"),
					fieldWithPath("data.nickName").description("유저 닉네임"),
					fieldWithPath("data.followerNum").description("유저 팔로워 수"),
					fieldWithPath("data.followingNum").description("유저 팔로잉 수"),
					fieldWithPath("data.isFollow").description("유저 팔로우 여부")
				)
			))
			.andExpect(status().isOk());
	}

	@Test
	void updateNickName() throws Exception {
		// given
		String object = objectMapper.writeValueAsString(
			UpdateNickNameRequest.builder().nickName("nickName").image("image").build());

		// when
		ResultActions result = mockMvc.perform(post(("/v1/users/nickname"))
			.content(object)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer " + token.getAccessToken())
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON));

		// then
		result
			.andDo(print())
			.andDo(document("v1/user/nickname",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
				requestFields(
					fieldWithPath("nickName").description("변경할 닉네임"),
					fieldWithPath("image").description("변경할 유저 이미지")
				)
			))
			.andExpect(status().isNoContent());
	}

	//    @Test
	//    void deleteUser() throws Exception {
	//        mockMvc.perform(delete("/v1/users")
	//                .header("Authorization", "Bearer " + token1.getAccessToken()))
	//                .andDo(print())
	//                .andDo(document("v1/user/delete",
	//                        preprocessRequest(prettyPrint()),
	//                        preprocessResponse(prettyPrint()),
	//                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token"))
	//                ))
	//                .andExpect(status().isNoContent());
	//    }

	@Test
	void getAlarmStatus() throws Exception {
		mockMvc.perform(get("/v1/users/alarm")
				.header("Authorization", "Bearer " + token.getAccessToken()))
			.andDo(print())
			.andDo(document("v1/user/alarm",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
				responseFields(fieldWithPath("data.alarmOn").description("유저 알람 상태"))
			))
			.andExpect(status().isOk());
	}

	@Test
	void alarmChange() throws Exception {
		String object = objectMapper.writeValueAsString(NoticeTokenRequest.builder()
			.noticeAgree(true)
			.deviceToken(noticeToken.getDeviceToken()).build());

		mockMvc.perform(patch("/v1/users/alarm")
				.header("Authorization", "Bearer " + token.getAccessToken())
				.content(object)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andDo(document("v1/user/alarm",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
				requestFields(
					fieldWithPath("deviceToken").description("유저의 디바이스 토큰"),
					fieldWithPath("noticeAgree").description("알림 동의 여부")),
				responseFields(fieldWithPath("data.alarmOn").description("유저 알람 상태"))
			))
			.andExpect(status().isNoContent());
	}
}
