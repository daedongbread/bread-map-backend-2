package com.depromeet.breadmapbackend.domain.auth;

import com.depromeet.breadmapbackend.domain.auth.dto.LoginRequest;
import com.depromeet.breadmapbackend.domain.auth.dto.LogoutRequest;
import com.depromeet.breadmapbackend.domain.auth.dto.RegisterRequest;
import com.depromeet.breadmapbackend.domain.auth.dto.ReissueRequest;
import com.depromeet.breadmapbackend.domain.notice.token.NoticeToken;
import com.depromeet.breadmapbackend.domain.user.OAuthInfo;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserInfo;
import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.utils.ControllerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends ControllerTest {
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
        noticeTokenRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    void login() throws Exception {
        String object = objectMapper.writeValueAsString(LoginRequest.builder()
                .type(OAuthType.GOOGLE).idToken("oAuthId1").build());

        given(authService.login(any())).willReturn(token);

        mockMvc.perform(post("/v1/auth/login")
                        .content(object)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("v1/auth/login",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("type").description("idToken 프로바이더 (GOOGLE, KAKAO, APPLE)"),
                                fieldWithPath("idToken").description("idToken")
                        ),
                        responseFields(
                                fieldWithPath("data.userId").description("유저 고유 번호"),
                                fieldWithPath("data.accessToken").description("엑세스 토큰"),
                                fieldWithPath("data.refreshToken").description("리프레시 토큰"),
                                fieldWithPath("data.accessTokenExpiredDate").description("엑세스 토큰 만료시간")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void register() throws Exception {
        String object = objectMapper.writeValueAsString(RegisterRequest.builder()
                .type(OAuthType.GOOGLE).idToken("oAuthId1")
                .isTermsOfServiceAgreed(Boolean.TRUE).isPersonalInfoCollectionAgreed(Boolean.TRUE)
                .isMarketingInfoReceptionAgreed(Boolean.FALSE).build());

        given(authService.register(any())).willReturn(token);

        mockMvc.perform(post("/v1/auth/register")
                        .content(object)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("v1/auth/register",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("type").description("idToken 프로바이더 (GOOGLE, KAKAO, APPLE)"),
                                fieldWithPath("idToken").description("idToken"),
                                fieldWithPath("isTermsOfServiceAgreed").description("서비스 이용약관 동의 여부 (반드시 True)"),
                                fieldWithPath("isPersonalInfoCollectionAgreed").description("개인정보 수집 및 이용 동의 여부 (반드시 True)"),
                                fieldWithPath("isMarketingInfoReceptionAgreed").description("마케팅 정보 수신 동의 여부")
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

        given(authService.reissue(any())).willReturn(token);

        // when
        ResultActions result = mockMvc.perform(post("/v1/auth/reissue")
                .content(object)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result
                .andDo(print())
                .andDo(document("v1/auth/reissue",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("accessToken").description("엑세스 토큰"),
                                fieldWithPath("refreshToken").description("리프레시 토큰")
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
    void logout() throws Exception {
        // given
        String object = objectMapper.writeValueAsString(LogoutRequest.builder()
                .accessToken(token.getAccessToken()).refreshToken(token.getRefreshToken())
                .deviceToken(noticeToken.getDeviceToken()).build());

        // when
        ResultActions result = mockMvc.perform(post("/v1/auth/logout")
                .header("Authorization", "Bearer " + token.getAccessToken())
                .content(object)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result
                .andDo(print())
                .andDo(document("v1/auth/logout",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        requestFields(
                                fieldWithPath("accessToken").description("엑세스 토큰"),
                                fieldWithPath("refreshToken").description("리프레시 토큰"),
                                fieldWithPath("deviceToken").description("디바이스 토큰")
                        )
                ))
                .andExpect(status().isNoContent());
    }
}