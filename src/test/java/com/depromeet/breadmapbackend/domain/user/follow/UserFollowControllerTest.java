package com.depromeet.breadmapbackend.domain.user.follow;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.bakery.product.Product;
import com.depromeet.breadmapbackend.domain.bakery.product.ProductType;
import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.depromeet.breadmapbackend.domain.notice.token.NoticeToken;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewImage;
import com.depromeet.breadmapbackend.domain.review.ReviewProductRating;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.block.BlockUser;
import com.depromeet.breadmapbackend.domain.user.dto.FollowRequest;
import com.depromeet.breadmapbackend.domain.user.follow.Follow;
import com.depromeet.breadmapbackend.global.ImageType;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.utils.ControllerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserFollowControllerTest extends ControllerTest {
    private User user1;
    private User user2;
    private JwtToken token1;
    private JwtToken token2;

    @BeforeEach
    public void setUp() {
        user1 = User.builder().username("testUserName1").nickName("testNickName1").roleType(RoleType.USER).build();
        user2 = User.builder().username("testUserName2").nickName("testNickName2").roleType(RoleType.USER).build();
        userRepository.save(user1);
        userRepository.save(user2);

        token1 = jwtTokenProvider.createJwtToken(user1.getUsername(), RoleType.USER.getCode());
        redisTemplate.opsForValue()
                .set(customRedisProperties.getKey().getRefresh() + ":" + user1.getUsername(),
                        token1.getRefreshToken(), jwtTokenProvider.getRefreshTokenExpiredDate(), TimeUnit.MILLISECONDS);
        token2 = jwtTokenProvider.createJwtToken(user2.getUsername(), RoleType.USER.getCode());

        Follow follow1 = Follow.builder().fromUser(user1).toUser(user2).build();
        Follow follow2 = Follow.builder().fromUser(user2).toUser(user1).build();
        followRepository.save(follow1);
        followRepository.save(follow2);
    }

    @AfterEach
    public void setDown() {
        followRepository.deleteAllInBatch();
        noticeRepository.deleteAllInBatch();
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
