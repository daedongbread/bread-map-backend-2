package com.depromeet.breadmapbackend.domain.user;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.bakery.product.Product;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.global.ImageType;
import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.depromeet.breadmapbackend.domain.notice.token.NoticeToken;
import com.depromeet.breadmapbackend.domain.bakery.product.ProductType;
import com.depromeet.breadmapbackend.domain.review.ReviewProductRating;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewImage;
import com.depromeet.breadmapbackend.domain.user.block.BlockUser;
import com.depromeet.breadmapbackend.domain.user.follow.Follow;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.utils.ControllerTest;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.domain.user.dto.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends ControllerTest {
    private User user;
    private JwtToken token;
    private NoticeToken noticeToken;

    @BeforeEach
    public void setUp() {
        user = User.builder().username("testUserName1").nickName("testNickName1").roleType(RoleType.USER).build();
        userRepository.save(user);

        token = jwtTokenProvider.createJwtToken(user.getUsername(), RoleType.USER.getCode());
        redisTemplate.opsForValue()
                .set(customRedisProperties.getKey().getRefresh() + ":" + user.getUsername(),
                        token.getRefreshToken(), jwtTokenProvider.getRefreshTokenExpiredDate(), TimeUnit.MILLISECONDS);

        noticeToken = NoticeToken.builder().user(user).deviceToken("deviceToken1").build();
        noticeTokenRepository.save(noticeToken);
    }

    @AfterEach
    public void setDown() {
        noticeTokenRepository.deleteAllInBatch();
        noticeRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
//    @Transactional
    void reissue() throws Exception {
        // given
        String object = objectMapper.writeValueAsString(ReissueRequest.builder()
                .accessToken(token.getAccessToken()).refreshToken(token.getRefreshToken()).build());

        // when
        ResultActions result = mockMvc.perform(post("/v1/users/auth/reissue")
                .content(object)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result
                .andDo(print())
                .andDo(document("v1/user/reissue",
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
        String object = objectMapper.writeValueAsString(UpdateNickNameRequest.builder().nickName("testtest").build());
        MockMultipartFile request =
                new MockMultipartFile("request", "", "application/json", object.getBytes());

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders
                .fileUpload(("/v1/users/nickname"))
                .file(new MockMultipartFile("file", UUID.randomUUID().toString() +".png", "image/png", "test".getBytes()))
                .file(request).accept(MediaType.APPLICATION_JSON)
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
                        requestParts(
                                partWithName("request").description("변경 닉네임 정보"),
                                partWithName("file").description("변경 유저 이미지")
                        ),
                        requestPartBody("request"),
                        requestPartFields("request",
                                fieldWithPath("nickName").description("변경할 닉네임")
                        )
                ))
                .andExpect(status().isNoContent());
    }

    @Test
    void logout() throws Exception {
        // given
        String object = objectMapper.writeValueAsString(LogoutRequest.builder()
                .accessToken(token.getAccessToken()).refreshToken(token.getRefreshToken())
                .deviceToken(noticeToken.getDeviceToken()).build());

        // when
        ResultActions result = mockMvc.perform(post("/v1/users/logout")
                .header("Authorization", "Bearer " + token.getAccessToken())
                .content(object)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result
                .andDo(print())
                .andDo(document("v1/user/logout",
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
    void updateAlarmStatus() throws Exception {
        mockMvc.perform(patch("/v1/users/alarm")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/user/alarm/update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token"))
                ))
                .andExpect(status().isNoContent());
    }
}
