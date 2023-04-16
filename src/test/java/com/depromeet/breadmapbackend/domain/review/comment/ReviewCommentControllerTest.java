package com.depromeet.breadmapbackend.domain.review.comment;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.comment.like.ReviewCommentLike;
import com.depromeet.breadmapbackend.domain.review.comment.dto.ReviewCommentRequest;
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
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReviewCommentControllerTest extends ControllerTest {
    private JwtToken token;
    private Review review;
    private ReviewComment comment1;
    private ReviewComment comment2;

    @BeforeEach
    public void setup() {
        User user = User.builder().oAuthInfo(OAuthInfo.builder().oAuthType(OAuthType.GOOGLE).oAuthId("oAuthId1").build())
                .userInfo(UserInfo.builder().nickName("nickname1").build()).build();
        userRepository.save(user);
        token = jwtTokenProvider.createJwtToken(user.getOAuthId(), user.getRoleType().getCode());

        List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);
        Bakery bakery = Bakery.builder().address("address").latitude(37.5596080725671).longitude(127.044235133983)
                .facilityInfoList(facilityInfo).name("bakery1").status(BakeryStatus.POSTING).build();
        bakeryRepository.save(bakery);

        review = Review.builder().user(user).bakery(bakery).content("content1").build();
        reviewRepository.save(review);

        comment1 = ReviewComment.builder().review(review).user(user).content("comment1").build();
        reviewCommentRepository.save(comment1);
        comment2 = ReviewComment.builder().review(review).user(user).content("comment2").parent(comment1).build();
        reviewCommentRepository.save(comment2);

        ReviewCommentLike reviewCommentLike = ReviewCommentLike.builder().reviewComment(comment1).user(user).build();
        reviewCommentLikeRepository.save(reviewCommentLike);
    }

    @AfterEach
    public void setDown() {
        reviewCommentLikeRepository.deleteAllInBatch();
        reviewCommentRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
        bakeryRepository.deleteAllInBatch();
        noticeRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
//    @Transactional
    void getReviewCommentList() throws Exception {
        mockMvc.perform(get("/v1/reviews/{reviewId}/comments", review.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/review/comment/all",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(parameterWithName("reviewId").description("리뷰 고유 번호")),
                        responseFields(
                                fieldWithPath("data.[].id").description("리뷰 고유 번호"),
                                fieldWithPath("data.[].userId").description("유저 고유 번호"),
                                fieldWithPath("data.[].userImage").description("유저 이미지"),
                                fieldWithPath("data.[].nickName").description("유저 닉네임"),
                                fieldWithPath("data.[].commentNickName").description("부모 댓글 유저 닉네임"),
                                fieldWithPath("data.[].content").description("댓글 내용"),
                                fieldWithPath("data.[].createdAt").description("댓글 생성일"),
                                fieldWithPath("data.[].likeNum").description("댓글 좋아요 수"),
                                fieldWithPath("data.[].commentList").description("대댓글 리스트"),
                                fieldWithPath("data.[].commentList.[].id").description("대댓글 고유 번호"),
                                fieldWithPath("data.[].commentList.[].userId").description("유저 고유 번호"),
                                fieldWithPath("data.[].commentList.[].userImage").description("유저 이미지"),
                                fieldWithPath("data.[].commentList.[].nickName").description("유저 닉네임"),
                                fieldWithPath("data.[].commentList.[].commentNickName").description("부모 댓글 유저 닉네임"),
                                fieldWithPath("data.[].commentList.[].content").description("대댓글 내용"),
                                fieldWithPath("data.[].commentList.[].createdAt").description("대댓글 생성일"),
                                fieldWithPath("data.[].commentList.[].likeNum").description("대댓글 좋아요 수"),
                                fieldWithPath("data.[].commentList.[].commentList").description("대댓글 리스트")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
//    @Transactional
    void addReviewComment() throws Exception{
        String object = objectMapper.writeValueAsString(ReviewCommentRequest.builder()
                .content("add review comment test").parentCommentId(0L).build());

        mockMvc.perform(post("/v1/reviews/{reviewId}/comments", review.getId())
                        .content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/review/comment/add",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(parameterWithName("reviewId").description("빵집 고유 번호")),
                        requestFields(
                                fieldWithPath("content").description("리뷰 댓글 내용"),
                                fieldWithPath("parentCommentId").description("리뷰 댓글의 부모 댓글 고유 번호 (리뷰에 단 댓글일 땐 0)")
                        )
                ))
                .andExpect(status().isCreated());
    }

    @Test
//    @Transactional
    void removeReviewComment() throws Exception {
        mockMvc.perform(delete("/v1/reviews/{reviewId}/comments/{commentId}", review.getId(), comment1.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/review/comment/remove",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(
                                parameterWithName("reviewId").description("리뷰 고유 번호"),
                                parameterWithName("commentId").description("댓글 고유 번호")
                        )
                ))
                .andExpect(status().isNoContent());
    }

    @Test
//    @Transactional
    void reviewCommentLike() throws Exception {
        mockMvc.perform(post("/v1/reviews/{reviewId}/comments/{commentId}/like", review.getId(), comment2.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/review/comment/like",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(
                                parameterWithName("reviewId").description("리뷰 고유 번호"),
                                parameterWithName("commentId").description("댓글 고유 번호")
                        )
                ))
                .andExpect(status().isCreated());
    }

    @Test
//    @Transactional
    void reviewCommentUnlike() throws Exception {
        mockMvc.perform(delete("/v1/reviews/{reviewId}/comments/{commentId}/unlike", review.getId(), comment1.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/review/comment/unlike",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(
                                parameterWithName("reviewId").description("리뷰 고유 번호"),
                                parameterWithName("commentId").description("댓글 고유 번호")
                        )
                ))
                .andExpect(status().isNoContent());
    }
}
