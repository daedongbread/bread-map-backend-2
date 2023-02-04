package com.depromeet.breadmapbackend.web.controller.user;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.product.Product;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.common.ImageType;
import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.depromeet.breadmapbackend.domain.notice.NoticeToken;
import com.depromeet.breadmapbackend.domain.product.ProductType;
import com.depromeet.breadmapbackend.domain.review.ReviewProductRating;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewImage;
import com.depromeet.breadmapbackend.domain.user.BlockUser;
import com.depromeet.breadmapbackend.domain.user.Follow;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.utils.ControllerTest;
import com.depromeet.breadmapbackend.security.domain.RoleType;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.web.controller.notice.dto.NoticeTokenRequest;
import com.depromeet.breadmapbackend.web.controller.user.dto.*;
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
    private User user1;
    private User user2;
    private User userToBlock;
    private JwtToken token1;
    private JwtToken token2;
    private NoticeToken noticeToken1;

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
                .set(customRedisProperties.getKey().getRefresh() + ":" + user1.getUsername(),
                        token1.getRefreshToken(), jwtTokenProvider.getRefreshTokenExpiredDate(), TimeUnit.MILLISECONDS);
        token2 = jwtTokenProvider.createJwtToken(user2.getUsername(), RoleType.USER.getCode());

        noticeToken1 = NoticeToken.builder().user(user1).deviceToken("deviceToken1").build();
        noticeTokenRepository.save(noticeToken1);

        Follow follow1 = Follow.builder().fromUser(user1).toUser(user2).build();
        Follow follow2 = Follow.builder().fromUser(user2).toUser(user1).build();
        followRepository.save(follow1);
        followRepository.save(follow2);

        BlockUser blockUser = BlockUser.builder().user(user1).blockUser(userToBlock).build();
        blockUserRepository.save(blockUser);
        
        Bakery bakery = Bakery.builder().id(1L).address("address").latitude(37.5596080725671).longitude(127.044235133983)
                .facilityInfoList(Collections.singletonList(FacilityInfo.PARKING)).name("bakery1").status(BakeryStatus.POSTING).build();
        bakery.updateImage("testImage");
        bakeryRepository.save(bakery);

        Flag flag = Flag.builder().user(user1).name("testFlagName").color(FlagColor.ORANGE).build();
        flagRepository.save(flag);

        Product product = Product.builder().bakery(bakery).productType(ProductType.BREAD).name("bread1").price("3000").build();
        productRepository.save(product);

        FlagBakery flagBakery = FlagBakery.builder().flag(flag).bakery(bakery).user(user1).build();
        flagBakeryRepository.save(flagBakery);

        Review review = Review.builder().user(user1).bakery(bakery).content("content1").build();
        ReviewImage image = ReviewImage.builder().review(review).bakery(bakery).imageType(ImageType.REVIEW_IMAGE).image("image1").build();
        review.addImage(image);
        reviewRepository.save(review);

        ReviewProductRating rating = ReviewProductRating.builder().bakery(bakery).product(product).review(review).rating(4L).build();
        reviewProductRatingRepository.save(rating);
    }

    @AfterEach
    public void setDown() {
        flagBakeryRepository.deleteAllInBatch();
        flagRepository.deleteAllInBatch();
        reviewProductRatingRepository.deleteAllInBatch();
        reviewCommentLikeRepository.deleteAllInBatch();
        reviewCommentRepository.deleteAllInBatch();
        reviewLikeRepository.deleteAllInBatch();
        reviewImageRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        bakeryRepository.deleteAllInBatch();
        followRepository.deleteAllInBatch();
        blockUserRepository.deleteAllInBatch();
        noticeTokenRepository.deleteAllInBatch();
        noticeRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
//    @Transactional
    void reissue() throws Exception {
        // given
        String object = objectMapper.writeValueAsString(ReissueRequest.builder()
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
        mockMvc.perform(get("/user/{userId}", user1.getId())
                .header("Authorization", "Bearer " + token2.getAccessToken()))
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
                                fieldWithPath("data.isFollow").description("유저 팔로우 여부"),
                                fieldWithPath("data.userFlagList").description("유저 깃발 리스트"),
                                fieldWithPath("data.userFlagList.[].flagId").description("유저 깃발 고유번호"),
                                fieldWithPath("data.userFlagList.[].name").description("유저 깃발 이름"),
                                fieldWithPath("data.userFlagList.[].color").description("유저 깃발 색깔"),
                                fieldWithPath("data.userFlagList.[].flagImageList").description("유저 깃발 이미지 리스트 (최대 3개)")
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
                .fileUpload(("/user/nickname"))
                .file(new MockMultipartFile("file", UUID.randomUUID().toString() +".png", "image/png", "test".getBytes()))
                .file(request).accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token1.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result
                .andDo(print())
                .andDo(document("user/nickname",
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
                .accessToken(token1.getAccessToken()).refreshToken(token1.getRefreshToken())
                .deviceToken(noticeToken1.getDeviceToken()).build());

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
                                fieldWithPath("accessToken").description("엑세스 토큰"),
                                fieldWithPath("refreshToken").description("리프레시 토큰"),
                                fieldWithPath("deviceToken").description("디바이스 토큰")
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
        followRepository.deleteAllInBatch();
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

        mockMvc.perform(delete("/user/follow")
                .content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token1.getAccessToken()))
                .andDo(print())
                .andDo(document("user/unfollow",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        requestFields(fieldWithPath("userId").description("언팔로우한 유저 고유번호"))
                ))
                .andExpect(status().isNoContent());
    }

    @Test
//    @Transactional
    void myFollowerList() throws Exception {
        mockMvc.perform(get("/user/follower")
                .header("Authorization", "Bearer " + token2.getAccessToken()))
                .andDo(print())
                .andDo(document("user/me/follower",
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
                                fieldWithPath("data.[].isMe").description("팔로워 유저 본인 여부 : 항상 false")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
//    @Transactional
    void myFollowingList() throws Exception {
        mockMvc.perform(get("/user/following")
                .header("Authorization", "Bearer " + token2.getAccessToken()))
                .andDo(print())
                .andDo(document("user/me/following",
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
                                fieldWithPath("data.[].isMe").description("팔로잉 유저 본인 여부 : 항상 false")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
//    @Transactional
    void otherFollowerList() throws Exception {
        mockMvc.perform(get("/user/{userId}/follower", user1.getId())
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
                                fieldWithPath("data.[].followerNum").description("팔로워 유저 팔로워 수"),
                                fieldWithPath("data.[].isFollow").description("팔로워 유저 팔로우 여부"),
                                fieldWithPath("data.[].isMe").description("팔로워 유저 본인 여부")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
//    @Transactional
    void otherFollowingList() throws Exception {
        mockMvc.perform(get("/user/{userId}/following", user1.getId())
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
                                fieldWithPath("data.[].followerNum").description("팔로잉 유저 팔로워 수"),
                                fieldWithPath("data.[].isFollow").description("팔로잉 유저 팔로우 여부"),
                                fieldWithPath("data.[].isMe").description("팔로잉 유저 본인 여부")
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

    @Test
    void getAlarmStatus() throws Exception {
        mockMvc.perform(get("/user/alarm")
                .header("Authorization", "Bearer " + token1.getAccessToken()))
                .andDo(print())
                .andDo(document("user/alarm",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        responseFields(fieldWithPath("data.alarmOn").description("유저 알람 상태"))
                ))
                .andExpect(status().isOk());
    }

    @Test
    void updateAlarmStatus() throws Exception {
        mockMvc.perform(patch("/user/alarm")
                .header("Authorization", "Bearer " + token1.getAccessToken()))
                .andDo(print())
                .andDo(document("user/alarm/update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token"))
                ))
                .andExpect(status().isNoContent());
    }
}
