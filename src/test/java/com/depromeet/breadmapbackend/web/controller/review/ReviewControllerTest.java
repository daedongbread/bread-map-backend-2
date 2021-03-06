package com.depromeet.breadmapbackend.web.controller.review;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.Bread;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.review.*;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.utils.ControllerTest;
import com.depromeet.breadmapbackend.security.domain.RoleType;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.web.controller.review.dto.ReviewCommentRequest;
import com.depromeet.breadmapbackend.web.controller.review.dto.ReviewReportRequest;
import com.depromeet.breadmapbackend.web.controller.review.dto.ReviewRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReviewControllerTest extends ControllerTest {
    private User user;
    private JwtToken token;
    private Bakery bakery;
    private Bread bread1;
    private Bread bread2;
    private Review review;
    private ReviewComment comment1;
    private ReviewComment comment2;

    @BeforeEach
    public void setup() {
        user = User.builder().nickName("nickname").roleType(RoleType.USER).username("username").build();
        userRepository.save(user);
        token = jwtTokenProvider.createJwtToken(user.getUsername(), user.getRoleType().getCode());

        List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);
        bakery = Bakery.builder().id(1L).domicileAddress("domicile").latitude(37.5596080725671).longitude(127.044235133983)
                .facilityInfoList(facilityInfo).name("bakery1").streetAddress("street").build();
        bakeryRepository.save(bakery);
        bread1 = Bread.builder().bakery(bakery).name("bread1").price(3000).build();
        bread2 = Bread.builder().bakery(bakery).name("bread2").price(4000).build();
        breadRepository.save(bread1);
        breadRepository.save(bread2);
        review = Review.builder().user(user).bakery(bakery).content("content1").isUse(true).build();
        review.addImage("image1");
        reviewRepository.save(review);
        BreadRating rating = BreadRating.builder().bread(bread1).review(review).rating(4L).build();
        breadRatingRepository.save(rating);

        ReviewLike reviewLike = ReviewLike.builder().review(review).user(user).build();
        reviewLikeRepository.save(reviewLike);

        comment1 = ReviewComment.builder().review(review).user(user).content("comment1").build();
        reviewCommentRepository.save(comment1);
        comment2 = ReviewComment.builder().review(review).user(user).content("comment1").parent(comment1).build();
        reviewCommentRepository.save(comment2);

        ReviewCommentLike reviewCommentLike = ReviewCommentLike.builder().reviewComment(comment1).user(user).build();
        reviewCommentLikeRepository.save(reviewCommentLike);
    }

    @AfterEach
    public void setDown() {
        reviewReportRepository.deleteAllInBatch();
        flagBakeryRepository.deleteAllInBatch();
        flagRepository.deleteAllInBatch();
        breadRatingRepository.deleteAllInBatch();
        reviewCommentLikeRepository.deleteAllInBatch();
        reviewCommentRepository.deleteAllInBatch();
        reviewLikeRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
        breadRepository.deleteAllInBatch();
        bakeryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
//    @Transactional
    void getBakeryReviewList() throws Exception {
        mockMvc.perform(get("/review/{bakeryId}/simple?sort=latest", bakery.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("review/get/simple",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("?????? ?????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("data.[].id").description("?????? ?????? ??????"),
                                fieldWithPath("data.[].userId").description("?????? ?????? ??????"),
                                fieldWithPath("data.[].userImage").description("?????? ?????????"),
                                fieldWithPath("data.[].nickName").description("?????? ?????????"),
                                fieldWithPath("data.[].reviewNum").description("?????? ?????? ???"),
                                fieldWithPath("data.[].followerNum").description("?????? ????????? ???"),
                                fieldWithPath("data.[].breadRatingDtoList").description("?????? ??? ?????? ?????????"),
                                fieldWithPath("data.[].breadRatingDtoList.[].breadName").description("?????? ??? ??????"),
                                fieldWithPath("data.[].breadRatingDtoList.[].rating").description("?????? ??? ??????"),
                                fieldWithPath("data.[].imageList").description("?????? ?????????"),
                                fieldWithPath("data.[].content").description("?????? ??????"),
                                fieldWithPath("data.[].likeNum").description("?????? ????????? ???"),
                                fieldWithPath("data.[].commentNum").description("?????? ?????? ???"),
                                fieldWithPath("data.[].createdAt").description("?????? ?????????"),
                                fieldWithPath("data.[].averageRating").description("?????? ?????? ??????")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
//    @Transactional
    void getAllBakeryReviewList() throws Exception{
        mockMvc.perform(get("/review/{bakeryId}/all?sort=latest", bakery.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("review/get/all",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("?????? ?????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("data.[].id").description("?????? ?????? ??????"),
                                fieldWithPath("data.[].userId").description("?????? ?????? ??????"),
                                fieldWithPath("data.[].userImage").description("?????? ?????????"),
                                fieldWithPath("data.[].nickName").description("?????? ?????????"),
                                fieldWithPath("data.[].reviewNum").description("?????? ?????? ???"),
                                fieldWithPath("data.[].followerNum").description("?????? ????????? ???"),
                                fieldWithPath("data.[].breadRatingDtoList").description("?????? ??? ?????? ?????????"),
                                fieldWithPath("data.[].breadRatingDtoList.[].breadName").description("?????? ??? ??????"),
                                fieldWithPath("data.[].breadRatingDtoList.[].rating").description("?????? ??? ??????"),
                                fieldWithPath("data.[].imageList").description("?????? ?????????"),
                                fieldWithPath("data.[].content").description("?????? ??????"),
                                fieldWithPath("data.[].likeNum").description("?????? ????????? ???"),
                                fieldWithPath("data.[].commentNum").description("?????? ?????? ???"),
                                fieldWithPath("data.[].createdAt").description("?????? ?????????"),
                                fieldWithPath("data.[].averageRating").description("?????? ?????? ??????")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
//    @Transactional
    void getReview() throws Exception{
        mockMvc.perform(get("/review/{reviewId}", review.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("review/get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        pathParameters(
                                parameterWithName("reviewId").description("?????? ?????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("data.id").description("?????? ?????? ??????"),
                                fieldWithPath("data.bakeryImage").description("?????? ?????????"),
                                fieldWithPath("data.bakeryName").description("?????? ??????"),
                                fieldWithPath("data.bakeryAddress").description("?????? ??????"),
                                fieldWithPath("data.userId").description("?????? ?????? ??????"),
                                fieldWithPath("data.userImage").description("?????? ?????????"),
                                fieldWithPath("data.nickName").description("?????? ?????????"),
                                fieldWithPath("data.reviewNum").description("?????? ?????? ???"),
                                fieldWithPath("data.followerNum").description("?????? ????????? ???"),
                                fieldWithPath("data.breadRatingDtoList").description("?????? ??? ?????? ?????????"),
                                fieldWithPath("data.breadRatingDtoList.[].breadName").description("?????? ??? ??????"),
                                fieldWithPath("data.breadRatingDtoList.[].rating").description("?????? ??? ??????"),
                                fieldWithPath("data.imageList").description("?????? ?????????"),
                                fieldWithPath("data.content").description("?????? ??????"),
                                fieldWithPath("data.likeNum").description("?????? ????????? ???"),
                                fieldWithPath("data.commentNum").description("?????? ?????? ???"),
                                fieldWithPath("data.createdAt").description("?????? ?????????"),
                                fieldWithPath("data.comments").description("?????? ?????? ?????????"),
                                fieldWithPath("data.comments.[].id").description("?????? ?????? ??????"),
                                fieldWithPath("data.comments.[].userId").description("?????? ?????? ??????"),
                                fieldWithPath("data.comments.[].userImage").description("?????? ?????????"),
                                fieldWithPath("data.comments.[].nickName").description("?????? ?????????"),
                                fieldWithPath("data.comments.[].commentNickName").description("?????? ?????? ?????? ?????????"),
                                fieldWithPath("data.comments.[].content").description("?????? ??????"),
                                fieldWithPath("data.comments.[].createdAt").description("?????? ?????????"),
                                fieldWithPath("data.comments.[].likeNum").description("?????? ????????? ???"),
                                fieldWithPath("data.comments.[].commentDtoList").description("????????? ?????????"),
                                fieldWithPath("data.comments.[].commentDtoList.[]").description("?????? ????????? ?????????"),
                                fieldWithPath("data.comments.[].commentDtoList.[].id").description("????????? ?????? ??????"),
                                fieldWithPath("data.comments.[].commentDtoList.[].userId").description("?????? ?????? ??????"),
                                fieldWithPath("data.comments.[].commentDtoList.[].userImage").description("?????? ?????????"),
                                fieldWithPath("data.comments.[].commentDtoList.[].nickName").description("?????? ?????????"),
                                fieldWithPath("data.comments.[].commentDtoList.[].commentNickName").description("?????? ?????? ?????? ?????????"),
                                fieldWithPath("data.comments.[].commentDtoList.[].content").description("????????? ??????"),
                                fieldWithPath("data.comments.[].commentDtoList.[].createdAt").description("????????? ?????????"),
                                fieldWithPath("data.comments.[].commentDtoList.[].likeNum").description("????????? ????????? ???"),
                                fieldWithPath("data.comments.[].commentDtoList.[].commentDtoList").description("????????? ?????????"),
                                fieldWithPath("data.userOtherReviews").description("?????? ?????? ?????? ?????? ?????????"),
                                fieldWithPath("data.userOtherReviews.[].id").description("?????? ?????? ??????"),
                                fieldWithPath("data.userOtherReviews.[].image").description("?????? ?????????"),
                                fieldWithPath("data.userOtherReviews.[].rating").description("?????? ??????"),
                                fieldWithPath("data.bakeryOtherReviews").description("?????? ?????? ?????? ?????????"),
                                fieldWithPath("data.bakeryOtherReviews.[].id").description("?????? ?????? ??????"),
                                fieldWithPath("data.bakeryOtherReviews.[].image").description("?????? ?????????"),
                                fieldWithPath("data.bakeryOtherReviews.[].rating").description("?????? ??????")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
//    @Transactional
    void addReview() throws Exception {
        String object = objectMapper.writeValueAsString(ReviewRequest.builder()
                .breadRatingList(Arrays.asList(
                        ReviewRequest.BreadRatingRequest.builder().breadId(bread1.getId()).rating(5L).build(),
                        ReviewRequest.BreadRatingRequest.builder().breadId(bread2.getId()).rating(4L).build()
                )).content("review add test").build());
        MockMultipartFile request =
                new MockMultipartFile("request", "", "application/json", object.getBytes());

        mockMvc.perform(RestDocumentationRequestBuilders
                .fileUpload("/review/{bakeryId}", bakery.getId())
                .file(new MockMultipartFile("files", UUID.randomUUID().toString() +".png", "image/png", "test".getBytes()))
                .file(request).accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("review/add",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        pathParameters(parameterWithName("bakeryId").description("?????? ?????? ??????")),
                        requestParts(
                                partWithName("request").description("?????? ??????"),
                                partWithName("files").description("?????? ????????????")
                        ),
                        requestPartBody("request"),
                        requestPartFields("request",
                                fieldWithPath("breadRatingList").description("?????? ??? ?????? ?????????"),
                                fieldWithPath("breadRatingList.[].breadId").description("?????? ??? ??????"),
                                fieldWithPath("breadRatingList.[].rating").description("?????? ??? ??????"),
                                fieldWithPath("content").description("?????? ??????")
                        )
                ))
                .andExpect(status().isCreated());
    }

    @Test
//    @Transactional
    void removeReview() throws Exception {
        mockMvc.perform(delete("/review/{reviewId}", review.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("review/remove",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        pathParameters(
                                parameterWithName("reviewId").description("?????? ?????? ??????")
                        )
                ))
                .andExpect(status().isNoContent());
    }

//    @Test
////    @Transactional
//    void getUserReviewList() throws Exception {
//        mockMvc.perform(get("/review")
//                .header("Authorization", "Bearer " + token.getAccessToken()))
//                .andDo(print())
//                .andDo(document("review/get/user",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint()),
//                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
//                        responseFields(
//                                fieldWithPath("data.[].id").description("?????? ?????? ??????"),
//                                fieldWithPath("data.[].bakeryName").description("?????? ??????"),
//                                fieldWithPath("data.[].bakeryAddress").description("?????? ??????"),
//                                fieldWithPath("data.[].breadRatingDtoList").description("?????? ??? ?????? ?????????"),
//                                fieldWithPath("data.[].breadRatingDtoList.[].breadName").description("?????? ??? ??????"),
//                                fieldWithPath("data.[].breadRatingDtoList.[].rating").description("?????? ??? ??????"),
//                                fieldWithPath("data.[].imageList").description("?????? ?????????"),
//                                fieldWithPath("data.[].content").description("?????? ??????"),
//                                fieldWithPath("data.[].likeNum").description("?????? ????????? ???"),
//                                fieldWithPath("data.[].commentNum").description("?????? ?????? ???"),
//                                fieldWithPath("data.[].createdAt").description("?????? ?????????")
//                        )
//                ))
//                .andExpect(status().isOk());
//    }

    @Test
//    @Transactional
    void reviewLike() throws Exception {
        Review review = Review.builder().user(user).bakery(bakery).content("new content").isUse(true).build();
        reviewRepository.save(review);

        mockMvc.perform(post("/review/{reviewId}/like", review.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("review/like",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        pathParameters(
                                parameterWithName("reviewId").description("?????? ?????? ??????")
                        )
                ))
                .andExpect(status().isCreated());
    }

    @Test
//    @Transactional
    void reviewUnlike() throws Exception {
        mockMvc.perform(delete("/review/{reviewId}/unlike", review.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("review/unlike",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        pathParameters(
                                parameterWithName("reviewId").description("?????? ?????? ??????")
                        )
                ))
                .andExpect(status().isNoContent());
    }

    @Test
//    @Transactional
    void getReviewCommentList() throws Exception {
        mockMvc.perform(get("/review/{reviewId}/comment", review.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("review/comment/all",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        pathParameters(
                                parameterWithName("reviewId").description("?????? ?????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("data.[].id").description("?????? ?????? ??????"),
                                fieldWithPath("data.[].userId").description("?????? ?????? ??????"),
                                fieldWithPath("data.[].userImage").description("?????? ?????????"),
                                fieldWithPath("data.[].nickName").description("?????? ?????????"),
                                fieldWithPath("data.[].commentNickName").description("?????? ?????? ?????? ?????????"),
                                fieldWithPath("data.[].content").description("?????? ??????"),
                                fieldWithPath("data.[].createdAt").description("?????? ?????????"),
                                fieldWithPath("data.[].likeNum").description("?????? ????????? ???"),
                                fieldWithPath("data.[].commentDtoList").description("????????? ?????????"),
                                fieldWithPath("data.[].commentDtoList.[].id").description("????????? ?????? ??????"),
                                fieldWithPath("data.[].commentDtoList.[].userId").description("?????? ?????? ??????"),
                                fieldWithPath("data.[].commentDtoList.[].userImage").description("?????? ?????????"),
                                fieldWithPath("data.[].commentDtoList.[].nickName").description("?????? ?????????"),
                                fieldWithPath("data.[].commentDtoList.[].commentNickName").description("?????? ?????? ?????? ?????????"),
                                fieldWithPath("data.[].commentDtoList.[].content").description("????????? ??????"),
                                fieldWithPath("data.[].commentDtoList.[].createdAt").description("????????? ?????????"),
                                fieldWithPath("data.[].commentDtoList.[].likeNum").description("????????? ????????? ???"),
                                fieldWithPath("data.[].commentDtoList.[].commentDtoList").description("????????? ?????????")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
//    @Transactional
    void addReviewComment() throws Exception{
        String object = objectMapper.writeValueAsString(ReviewCommentRequest.builder()
                .content("add review comment test").parentCommentId(0L).build());

        mockMvc.perform(post("/review/{reviewId}/comment", review.getId())
                .content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("review/comment/add",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        pathParameters(
                                parameterWithName("reviewId").description("?????? ?????? ??????")
                        ),
                        requestFields(
                                fieldWithPath("content").description("?????? ?????? ??????"),
                                fieldWithPath("parentCommentId").description("?????? ????????? ?????? ?????? ?????? ??????")
                        )
                ))
                .andExpect(status().isCreated());
    }

    @Test
//    @Transactional
    void removeReviewComment() throws Exception {
        mockMvc.perform(delete("/review/{reviewId}/comment/{commentId}", review.getId(), comment1.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("review/comment/remove",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        pathParameters(
                                parameterWithName("reviewId").description("?????? ?????? ??????"),
                                parameterWithName("commentId").description("?????? ?????? ??????")
                        )
                ))
                .andExpect(status().isNoContent());
    }

    @Test
//    @Transactional
    void reviewCommentLike() throws Exception {
        mockMvc.perform(post("/review/{reviewId}/comment/{commentId}/like", review.getId(), comment2.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("review/comment/like",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        pathParameters(
                                parameterWithName("reviewId").description("?????? ?????? ??????"),
                                parameterWithName("commentId").description("?????? ?????? ??????")
                        )
                ))
                .andExpect(status().isCreated());
    }

    @Test
//    @Transactional
    void reviewCommentUnlike() throws Exception {
        mockMvc.perform(delete("/review/{reviewId}/comment/{commentId}/unlike", review.getId(), comment1.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("review/comment/unlike",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        pathParameters(
                                parameterWithName("reviewId").description("?????? ?????? ??????"),
                                parameterWithName("commentId").description("?????? ?????? ??????")
                        )
                ))
                .andExpect(status().isNoContent());
    }

    @Test
    void reviewReport() throws Exception {
        String object = objectMapper.writeValueAsString(
                ReviewReportRequest.builder().reason(ReviewReportReason.COPYRIGHT_THEFT).content("Copyright").build());

        mockMvc.perform(post("/review/{reviewId}/report", review.getId())
                .header("Authorization", "Bearer " + token.getAccessToken())
                .content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("review/report",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        pathParameters(parameterWithName("reviewId").description("?????? ?????? ??????")),
                        requestFields(
                                fieldWithPath("reason").description("?????? ?????? ??????"),
                                fieldWithPath("content").description("?????? ?????? ??????")
                        )
                ))
                .andExpect(status().isCreated());
    }
}