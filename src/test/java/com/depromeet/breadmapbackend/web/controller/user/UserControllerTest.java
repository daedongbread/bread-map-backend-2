package com.depromeet.breadmapbackend.web.controller.user;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.Bread;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.depromeet.breadmapbackend.domain.review.BreadRating;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.user.BlockUser;
import com.depromeet.breadmapbackend.domain.user.Follow;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.utils.ControllerTest;
import com.depromeet.breadmapbackend.security.domain.RoleType;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.web.controller.user.dto.BlockRequest;
import com.depromeet.breadmapbackend.web.controller.user.dto.FollowRequest;
import com.depromeet.breadmapbackend.web.controller.user.dto.TokenRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends ControllerTest {
    private User user1;
    private User user2;
    private User userToBlock;
    private JwtToken token1;
    private JwtToken token2;

    @BeforeEach
    public void setUp() {
        user1 = User.builder().username("testUserName1").nickName("testNickName1").roleType(RoleType.USER).build();
        user2 = User.builder().username("testUserName2").nickName("testNickName2").roleType(RoleType.USER).build();
        userToBlock = User.builder().username("testBlockUserName").nickName("testBlockUserNickName").roleType(RoleType.USER).build();
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(userToBlock);

        token1 = jwtTokenProvider.createJwtToken(user1.getUsername(), RoleType.USER.getCode());
        redisTemplate.opsForValue()
                .set(REDIS_KEY_REFRESH + user1.getUsername(),
                        token1.getRefreshToken(), jwtTokenProvider.getRefreshTokenExpiredDate(), TimeUnit.MILLISECONDS);
        token2 = jwtTokenProvider.createJwtToken(user2.getUsername(), RoleType.USER.getCode());

        Follow follow = Follow.builder().fromUser(user1).toUser(user2).build();
        followRepository.save(follow);

        BlockUser blockUser = BlockUser.builder().user(user1).blockUser(userToBlock).build();
        blockUserRepository.save(blockUser);
        
        Bakery bakery = Bakery.builder().id(1L).domicileAddress("domicile").latitude(37.5596080725671).longitude(127.044235133983)
                .facilityInfoList(Collections.singletonList(FacilityInfo.PARKING)).name("bakery1")
                .streetAddress("street").image("testImage").build();
        bakeryRepository.save(bakery);

        Flag flag = Flag.builder().user(user1).name("testFlagName").color(FlagColor.ORANGE).build();
        flagRepository.save(flag);

        Bread bread = Bread.builder().bakery(bakery).name("bread1").price(3000).build();
        breadRepository.save(bread);

        FlagBakery flagBakery = FlagBakery.builder().flag(flag).bakery(bakery).build();
        flagBakeryRepository.save(flagBakery);

        Review review = Review.builder().user(user1).bakery(bakery).content("content1").isUse(true).build();
        review.addImage("reviewImage1");
        reviewRepository.save(review);

        BreadRating rating = BreadRating.builder().bread(bread).review(review).rating(4L).build();
        breadRatingRepository.save(rating);
    }

    @AfterEach
    public void setDown() {
        flagBakeryRepository.deleteAllInBatch();
        flagRepository.deleteAllInBatch();
        breadRatingRepository.deleteAllInBatch();
        reviewCommentLikeRepository.deleteAllInBatch();
        reviewCommentRepository.deleteAllInBatch();
        reviewLikeRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
        breadRepository.deleteAllInBatch();
        bakeryRepository.deleteAllInBatch();
        followRepository.deleteAllInBatch();
        blockUserRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
//    @Transactional
    void refresh() throws Exception {
        // given
        String object = objectMapper.writeValueAsString(TokenRequest.builder()
                .accessToken(token1.getAccessToken()).refreshToken(token1.getRefreshToken()).build());

        // when
        ResultActions result = mockMvc.perform(post("/user/auth/reissue")
                .content(object)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result
                .andDo(print())
                .andDo(document("user/reissue",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("엑세스 토큰"),
                                fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰")
                        ),
                        responseFields(
                                fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("엑세스 토큰"),
                                fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰"),
                                fieldWithPath("data.accessTokenExpiredDate").type(JsonFieldType.NUMBER).description("엑세스 토큰 만료시간")
                        )
                ))
                .andExpect(status().isCreated());
    }

    @Test
//    @Transactional
    void profile() throws Exception {
        mockMvc.perform(get("/user/profile")
                .header("Authorization", "Bearer " + token1.getAccessToken()))
                .andDo(print())
                .andDo(document("user/profile",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        responseFields(
                                fieldWithPath("data.userId").description("유저 고유 번호"),
                                fieldWithPath("data.userImage").description("유저 이미지"),
                                fieldWithPath("data.nickName").description("유저 닉네임"),
                                fieldWithPath("data.followerNum").description("유저 팔로워 수"),
                                fieldWithPath("data.followingNum").description("유저 팔로잉 수"),
                                fieldWithPath("data.userFlagDtoList").description("유저 깃발 리스트"),
                                fieldWithPath("data.userFlagDtoList.[].flagId").description("유저 깃발 고유번호"),
                                fieldWithPath("data.userFlagDtoList.[].name").description("유저 깃발 이름"),
                                fieldWithPath("data.userFlagDtoList.[].color").description("유저 깃발 색깔"),
                                fieldWithPath("data.userFlagDtoList.[].flagImageList").description("유저 깃발 이미지 리스트"),
                                fieldWithPath("data.userReviewDtoList").description("유저 리뷰 리스트"),
                                fieldWithPath("data.userReviewDtoList.[].id").description("유저 리뷰 고유 번호"),
                                fieldWithPath("data.userReviewDtoList.[].bakeryName").description("유저 빵집 이름"),
                                fieldWithPath("data.userReviewDtoList.[].bakeryAddress").description("유저 빵집 주소"),
                                fieldWithPath("data.userReviewDtoList.[].breadRatingDtoList").description("유저 리뷰 빵 점수 리스트"),
                                fieldWithPath("data.userReviewDtoList.[].breadRatingDtoList.[].breadName").description("유저 리뷰 빵 이름"),
                                fieldWithPath("data.userReviewDtoList.[].breadRatingDtoList.[].rating").description("유저 리뷰 빵 점수"),
                                fieldWithPath("data.userReviewDtoList.[].imageList").description("유저 리뷰 이미지"),
                                fieldWithPath("data.userReviewDtoList.[].content").description("유저 리뷰 내용"),
                                fieldWithPath("data.userReviewDtoList.[].likeNum").description("유저 리뷰 좋아요 수"),
                                fieldWithPath("data.userReviewDtoList.[].commentNum").description("유저 리뷰 댓글 수"),
                                fieldWithPath("data.userReviewDtoList.[].createdAt").description("유저 리뷰 생성일")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void logout() throws Exception {
        // given
        String object = objectMapper.writeValueAsString(TokenRequest.builder()
                .accessToken(token1.getAccessToken()).refreshToken(token1.getRefreshToken()).build());

        // when
        ResultActions result = mockMvc.perform(post("/user/logout")
                .header("Authorization", "Bearer " + token1.getAccessToken())
                .content(object)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result
                .andDo(print())
                .andDo(document("user/logout",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        requestFields(
                                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("엑세스 토큰"),
                                fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰")
                        )
                ))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/user")
                .header("Authorization", "Bearer " + token2.getAccessToken()))
                .andDo(print())
                .andDo(document("user/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token"))
                ))
                .andExpect(status().isNoContent());
    }

    @Test
//    @Transactional
    void follow() throws Exception {
        String object = objectMapper.writeValueAsString(FollowRequest.builder().userId(user1.getId()).build());

        mockMvc.perform(post("/user/follow")
                .content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token2.getAccessToken()))
                .andDo(print())
                .andDo(document("user/follow",
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

        mockMvc.perform(delete("/user/unfollow")
                .content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token1.getAccessToken()))
                .andDo(print())
                .andDo(document("user/unfollow",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        requestFields(fieldWithPath("userId").description("언팔로우할 유저 고유번호"))
                ))
                .andExpect(status().isNoContent());
    }

    @Test
//    @Transactional
    void followerList() throws Exception {
        mockMvc.perform(get("/user/follower")
                .header("Authorization", "Bearer " + token2.getAccessToken()))
                .andDo(print())
                .andDo(document("user/follower",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        responseFields(
                                fieldWithPath("data.[].userId").description("팔로워 유저 고유번호"),
                                fieldWithPath("data.[].userImage").description("팔로워 유저 이미지"),
                                fieldWithPath("data.[].nickName").description("팔로워 유저 닉네임"),
                                fieldWithPath("data.[].reviewNum").description("팔로워 유저 리뷰 수"),
                                fieldWithPath("data.[].followerNum").description("팔로워 유저 팔로워 수")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
//    @Transactional
    void followingList() throws Exception {
        mockMvc.perform(get("/user/following")
                .header("Authorization", "Bearer " + token2.getAccessToken()))
                .andDo(print())
                .andDo(document("user/following",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        responseFields(
                                fieldWithPath("data.[].userId").description("팔로잉 유저 고유번호"),
                                fieldWithPath("data.[].userImage").description("팔로잉 유저 이미지"),
                                fieldWithPath("data.[].nickName").description("팔로잉 유저 닉네임"),
                                fieldWithPath("data.[].reviewNum").description("팔로잉 유저 리뷰 수"),
                                fieldWithPath("data.[].followerNum").description("팔로잉 유저 팔로워 수")
                        )
                ))
                .andExpect(status().isOk());
    }


    @Test
//    @Transactional
    void blockList() throws Exception {
        mockMvc.perform(get("/user/block")
                .header("Authorization", "Bearer " + token1.getAccessToken()))
                .andDo(print())
                .andDo(document("user/blockList",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        responseFields(
                                fieldWithPath("data.[].userId").description("차단 유저 고유번호"),
                                fieldWithPath("data.[].userImage").description("차단 유저 이미지"),
                                fieldWithPath("data.[].nickName").description("차단 유저 닉네임"),
                                fieldWithPath("data.[].reviewNum").description("차단 유저 리뷰 수"),
                                fieldWithPath("data.[].followerNum").description("차단 유저 팔로워 수")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
//    @Transactional
    void block() throws Exception {
        String object = objectMapper.writeValueAsString(BlockRequest.builder().userId(user2.getId()).build());

        mockMvc.perform(post("/user/block")
                .content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token1.getAccessToken()))
                .andDo(print())
                .andDo(document("user/block",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        requestFields(fieldWithPath("userId").description("차단할 유저 고유번호"))
                ))
                .andExpect(status().isCreated());
    }

    @Test
//    @Transactional
    void unblock() throws Exception {
        String object = objectMapper.writeValueAsString(BlockRequest.builder().userId(userToBlock.getId()).build());

        mockMvc.perform(delete("/user/block")
                .content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token1.getAccessToken()))
                .andDo(print())
                .andDo(document("user/unblock",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        requestFields(fieldWithPath("userId").description("차단 해제할 유저 고유번호"))
                ))
                .andExpect(status().isNoContent());
    }
}
