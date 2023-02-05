package com.depromeet.breadmapbackend.web.controller.review;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.product.Product;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.common.ImageType;
import com.depromeet.breadmapbackend.domain.product.ProductType;
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
    private Product product1;
    private Product product2;
    private Review review1;
    private Review review2;
    private ReviewComment comment1;
    private ReviewComment comment2;

    @BeforeEach
    public void setup() {
        user = User.builder().nickName("nickname").roleType(RoleType.USER).username("username").build();
        userRepository.save(user);
        token = jwtTokenProvider.createJwtToken(user.getUsername(), user.getRoleType().getCode());

        List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);
        bakery = Bakery.builder().id(1L).address("address").latitude(37.5596080725671).longitude(127.044235133983)
                .facilityInfoList(facilityInfo).name("bakery1").status(BakeryStatus.POSTING).build();
        bakeryRepository.save(bakery);
        product1 = Product.builder().bakery(bakery).productType(ProductType.BREAD).name("bread1").price("3000").build();
        product2 = Product.builder().bakery(bakery).productType(ProductType.BREAD).name("bread2").price("4000").build();
        productRepository.save(product1);
        productRepository.save(product2);

        review1 = Review.builder().user(user).bakery(bakery).content("content1").build();
        ReviewImage image1 = ReviewImage.builder().review(review1).bakery(bakery).imageType(ImageType.REVIEW_IMAGE).image("image1").build();
        review1.addImage(image1);
        reviewRepository.save(review1);
        review2 = Review.builder().user(user).bakery(bakery).content("content2").build();
        ReviewImage image2 = ReviewImage.builder().review(review2).bakery(bakery).imageType(ImageType.REVIEW_IMAGE).image("image2").build();
        review2.addImage(image2);
        reviewRepository.save(review2);

        ReviewProductRating rating1 = ReviewProductRating.builder().bakery(bakery).product(product1).review(review1).rating(5L).build();
        reviewProductRatingRepository.save(rating1);
        ReviewProductRating rating2 = ReviewProductRating.builder().bakery(bakery).product(product2).review(review1).rating(4L).build();
        reviewProductRatingRepository.save(rating2);
        ReviewProductRating rating3 = ReviewProductRating.builder().bakery(bakery).product(product1).review(review2).rating(4L).build();
        reviewProductRatingRepository.save(rating3);
        ReviewProductRating rating4 = ReviewProductRating.builder().bakery(bakery).product(product2).review(review2).rating(3L).build();
        reviewProductRatingRepository.save(rating4);

        ReviewLike reviewLike = ReviewLike.builder().review(review1).user(user).build();
        reviewLikeRepository.save(reviewLike);

        comment1 = ReviewComment.builder().review(review1).user(user).content("comment1").build();
        reviewCommentRepository.save(comment1);
        comment2 = ReviewComment.builder().review(review1).user(user).content("comment2").parent(comment1).build();
        reviewCommentRepository.save(comment2);

        ReviewCommentLike reviewCommentLike = ReviewCommentLike.builder().reviewComment(comment1).user(user).build();
        reviewCommentLikeRepository.save(reviewCommentLike);
    }

    @AfterEach
    public void setDown() {
        reviewReportRepository.deleteAllInBatch();
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
        noticeTokenRepository.deleteAllInBatch();
        noticeRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
//    @Transactional
    void getBakeryReviewList() throws Exception{
        mockMvc.perform(get("/review/bakery/{bakeryId}?sortBy=high&page=0", bakery.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("review/get/bakery",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호")),
                        requestParameters(
                                parameterWithName("sortBy").description("정렬 방법 (latest, high, low) (default = latest)"),
                                parameterWithName("lastId").optional().description("지난 리뷰 마지막 고유 번호 (최신순(latest) 조회 시 두번째 페이지부터 필요)"),
                                parameterWithName("lastRating").optional().description("지난 리뷰 마지막 평균점수 (별점(high, low) 조회 시 두번째 페이지부터 필요"),
                                parameterWithName("page").description("현재 페이지 번호 (0부터)")),
                        responseFields(
                                fieldWithPath("data.pageNumber").description("현재 페이지 (0부터 시작)"),
                                fieldWithPath("data.numberOfElements").description("현재 페이지 데이터 수"),
                                fieldWithPath("data.size").description("페이지 크기"),
                                fieldWithPath("data.totalElements").description("전체 데이터 수"),
                                fieldWithPath("data.totalPages").description("전체 페이지 수"),
                                fieldWithPath("data.contents").description("리뷰 리스트"),
                                fieldWithPath("data.contents.[].bakeryInfo").description("리뷰 빵집 정보"),
                                fieldWithPath("data.contents.[].bakeryInfo.bakeryId").description("빵집 고유 번호"),
                                fieldWithPath("data.contents.[].bakeryInfo.bakeryImage").description("빵집 이미지"),
                                fieldWithPath("data.contents.[].bakeryInfo.bakeryName").description("빵집 이름"),
                                fieldWithPath("data.contents.[].bakeryInfo.bakeryAddress").description("빵집 주소"),
                                fieldWithPath("data.contents").description("리뷰 리스트"),
                                fieldWithPath("data.contents.[].userInfo").description("리뷰 유저 정보"),
                                fieldWithPath("data.contents.[].userInfo.userId").description("유저 고유 번호"),
                                fieldWithPath("data.contents.[].userInfo.userImage").description("유저 이미지"),
                                fieldWithPath("data.contents.[].userInfo.nickName").description("유저 닉네임"),
                                fieldWithPath("data.contents.[].userInfo.reviewNum").description("유저 리뷰 수"),
                                fieldWithPath("data.contents.[].userInfo.followerNum").description("유저 팔로워 수"),
                                fieldWithPath("data.contents.[].userInfo.isFollow").description("유저 팔로우 여부"),
                                fieldWithPath("data.contents.[].userInfo.isMe").description("유저 본인 여부"),
                                fieldWithPath("data.contents.[].reviewInfo").description("리뷰 정보"),
                                fieldWithPath("data.contents.[].reviewInfo.id").description("리뷰 고유 번호"),
                                fieldWithPath("data.contents.[].reviewInfo.productRatingList").description("리뷰 상품 점수 리스트"),
                                fieldWithPath("data.contents.[].reviewInfo.productRatingList.[].productName").description("리뷰 상품 이름"),
                                fieldWithPath("data.contents.[].reviewInfo.productRatingList.[].rating").description("리뷰 상품 점수"),
                                fieldWithPath("data.contents.[].reviewInfo.imageList").description("리뷰 이미지"),
                                fieldWithPath("data.contents.[].reviewInfo.content").description("리뷰 내용"),
                                fieldWithPath("data.contents.[].reviewInfo.likeNum").description("리뷰 좋아요 수"),
                                fieldWithPath("data.contents.[].reviewInfo.commentNum").description("리뷰 댓글 수"),
                                fieldWithPath("data.contents.[].reviewInfo.createdAt").description("리뷰 생성일"),
                                fieldWithPath("data.contents.[].reviewInfo.averageRating").description("리뷰 평균 점수")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
//    @Transactional
    void getProductReviewList() throws Exception{
        mockMvc.perform(get("/review/bakery/{bakeryId}/product/{productId}?sortBy=low&page=0", bakery.getId(), product1.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("review/get/product",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호"),
                                parameterWithName("productId").description("상품 고유 번호")),
                        requestParameters(
                                parameterWithName("sortBy").description("정렬 방법 (latest, high, low) (default = latest)"),
                                parameterWithName("lastId").optional().description("지난 리뷰 마지막 고유 번호 (최신순(latest) 조회 시 두번째 페이지부터 필요)"),
                                parameterWithName("lastRating").optional().description("지난 리뷰 마지막 평균점수 (별점(high, low) 조회 시 두번째 페이지부터 필요"),
                                parameterWithName("page").description("현재 페이지 번호 (0부터)")),
                        responseFields(
                                fieldWithPath("data.pageNumber").description("현재 페이지 (0부터 시작)"),
                                fieldWithPath("data.numberOfElements").description("현재 페이지 데이터 수"),
                                fieldWithPath("data.size").description("페이지 크기"),
                                fieldWithPath("data.totalElements").description("전체 데이터 수"),
                                fieldWithPath("data.totalPages").description("전체 페이지 수"),
                                fieldWithPath("data.contents").description("리뷰 리스트"),
                                fieldWithPath("data.contents.[].bakeryInfo").description("리뷰 빵집 정보"),
                                fieldWithPath("data.contents.[].bakeryInfo.bakeryId").description("빵집 고유 번호"),
                                fieldWithPath("data.contents.[].bakeryInfo.bakeryImage").description("빵집 이미지"),
                                fieldWithPath("data.contents.[].bakeryInfo.bakeryName").description("빵집 이름"),
                                fieldWithPath("data.contents.[].bakeryInfo.bakeryAddress").description("빵집 주소"),
                                fieldWithPath("data.contents.[].userInfo").description("리뷰 유저 정보"),
                                fieldWithPath("data.contents.[].userInfo.userId").description("유저 고유 번호"),
                                fieldWithPath("data.contents.[].userInfo.userImage").description("유저 이미지"),
                                fieldWithPath("data.contents.[].userInfo.nickName").description("유저 닉네임"),
                                fieldWithPath("data.contents.[].userInfo.reviewNum").description("유저 리뷰 수"),
                                fieldWithPath("data.contents.[].userInfo.followerNum").description("유저 팔로워 수"),
                                fieldWithPath("data.contents.[].userInfo.isFollow").description("유저 팔로우 여부"),
                                fieldWithPath("data.contents.[].userInfo.isMe").description("유저 본인 여부"),
                                fieldWithPath("data.contents.[].reviewInfo").description("리뷰 정보"),
                                fieldWithPath("data.contents.[].reviewInfo.id").description("리뷰 고유 번호"),
                                fieldWithPath("data.contents.[].reviewInfo.productRatingList").description("리뷰 상품 점수 리스트"),
                                fieldWithPath("data.contents.[].reviewInfo.productRatingList.[].productName").description("리뷰 상품 이름"),
                                fieldWithPath("data.contents.[].reviewInfo.productRatingList.[].rating").description("리뷰 상품 점수"),
                                fieldWithPath("data.contents.[].reviewInfo.imageList").description("리뷰 이미지"),
                                fieldWithPath("data.contents.[].reviewInfo.content").description("리뷰 내용"),
                                fieldWithPath("data.contents.[].reviewInfo.likeNum").description("리뷰 좋아요 수"),
                                fieldWithPath("data.contents.[].reviewInfo.commentNum").description("리뷰 댓글 수"),
                                fieldWithPath("data.contents.[].reviewInfo.createdAt").description("리뷰 생성일"),
                                fieldWithPath("data.contents.[].reviewInfo.averageRating").description("리뷰 평균 점수")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
//    @Transactional
    void getUserReviewList() throws Exception{
        mockMvc.perform(get("/review/user/{userId}?page=0", user.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("review/get/user",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(
                                parameterWithName("userId").description("유저 고유 번호")),
                        requestParameters(
                                parameterWithName("page").description("현재 페이지 번호 (0부터)")),
                        responseFields(
                                fieldWithPath("data.pageNumber").description("현재 페이지 (0부터 시작)"),
                                fieldWithPath("data.numberOfElements").description("현재 페이지 데이터 수"),
                                fieldWithPath("data.size").description("페이지 크기"),
                                fieldWithPath("data.totalElements").description("전체 데이터 수"),
                                fieldWithPath("data.totalPages").description("전체 페이지 수"),
                                fieldWithPath("data.contents").description("리뷰 리스트"),
                                fieldWithPath("data.contents.[].bakeryInfo").description("리뷰 빵집 정보"),
                                fieldWithPath("data.contents.[].bakeryInfo.bakeryId").description("빵집 고유 번호"),
                                fieldWithPath("data.contents.[].bakeryInfo.bakeryImage").description("빵집 이미지"),
                                fieldWithPath("data.contents.[].bakeryInfo.bakeryName").description("빵집 이름"),
                                fieldWithPath("data.contents.[].bakeryInfo.bakeryAddress").description("빵집 주소"),
                                fieldWithPath("data.contents.[].userInfo").description("리뷰 유저 정보"),
                                fieldWithPath("data.contents.[].userInfo.userId").description("유저 고유 번호"),
                                fieldWithPath("data.contents.[].userInfo.userImage").description("유저 이미지"),
                                fieldWithPath("data.contents.[].userInfo.nickName").description("유저 닉네임"),
                                fieldWithPath("data.contents.[].userInfo.reviewNum").description("유저 리뷰 수"),
                                fieldWithPath("data.contents.[].userInfo.followerNum").description("유저 팔로워 수"),
                                fieldWithPath("data.contents.[].userInfo.isFollow").description("유저 팔로우 여부"),
                                fieldWithPath("data.contents.[].userInfo.isMe").description("유저 본인 여부"),
                                fieldWithPath("data.contents.[].reviewInfo").description("리뷰 정보"),
                                fieldWithPath("data.contents.[].reviewInfo.id").description("리뷰 고유 번호"),
                                fieldWithPath("data.contents.[].reviewInfo.productRatingList").description("리뷰 상품 점수 리스트"),
                                fieldWithPath("data.contents.[].reviewInfo.productRatingList.[].productName").description("리뷰 상품 이름"),
                                fieldWithPath("data.contents.[].reviewInfo.productRatingList.[].rating").description("리뷰 상품 점수"),
                                fieldWithPath("data.contents.[].reviewInfo.imageList").description("리뷰 이미지"),
                                fieldWithPath("data.contents.[].reviewInfo.content").description("리뷰 내용"),
                                fieldWithPath("data.contents.[].reviewInfo.likeNum").description("리뷰 좋아요 수"),
                                fieldWithPath("data.contents.[].reviewInfo.commentNum").description("리뷰 댓글 수"),
                                fieldWithPath("data.contents.[].reviewInfo.createdAt").description("리뷰 생성일"),
                                fieldWithPath("data.contents.[].reviewInfo.averageRating").description("리뷰 평균 점수")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
//    @Transactional
    void getReview() throws Exception{
        mockMvc.perform(get("/review/{reviewId}", review1.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("review/get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(
                                parameterWithName("reviewId").description("리뷰 고유 번호")
                        ),
                        responseFields(
                                fieldWithPath("data.reviewDto").description("리뷰 상세 정보"),
                                fieldWithPath("data.reviewDto.bakeryInfo").description("리뷰 빵집 정보"),
                                fieldWithPath("data.reviewDto.bakeryInfo.bakeryId").description("빵집 고유 번호"),
                                fieldWithPath("data.reviewDto.bakeryInfo.bakeryImage").description("빵집 이미지"),
                                fieldWithPath("data.reviewDto.bakeryInfo.bakeryName").description("빵집 이름"),
                                fieldWithPath("data.reviewDto.bakeryInfo.bakeryAddress").description("빵집 주소"),
                                fieldWithPath("data.reviewDto.userInfo").description("리뷰 유저 정보"),
                                fieldWithPath("data.reviewDto.userInfo.userId").description("유저 고유 번호"),
                                fieldWithPath("data.reviewDto.userInfo.userImage").description("유저 이미지"),
                                fieldWithPath("data.reviewDto.userInfo.nickName").description("유저 닉네임"),
                                fieldWithPath("data.reviewDto.userInfo.reviewNum").description("유저 리뷰 수"),
                                fieldWithPath("data.reviewDto.userInfo.followerNum").description("유저 팔로워 수"),
                                fieldWithPath("data.reviewDto.userInfo.isFollow").description("유저 팔로우 여부"),
                                fieldWithPath("data.reviewDto.userInfo.isMe").description("유저 본인 여부"),
                                fieldWithPath("data.reviewDto.reviewInfo").description("리뷰 정보"),
                                fieldWithPath("data.reviewDto.reviewInfo.id").description("리뷰 고유 번호"),
                                fieldWithPath("data.reviewDto.reviewInfo.productRatingList").description("리뷰 상품 점수 리스트"),
                                fieldWithPath("data.reviewDto.reviewInfo.productRatingList.[].productName").description("리뷰 상품 이름"),
                                fieldWithPath("data.reviewDto.reviewInfo.productRatingList.[].rating").description("리뷰 상품 점수"),
                                fieldWithPath("data.reviewDto.reviewInfo.imageList").description("리뷰 이미지"),
                                fieldWithPath("data.reviewDto.reviewInfo.content").description("리뷰 내용"),
                                fieldWithPath("data.reviewDto.reviewInfo.likeNum").description("리뷰 좋아요 수"),
                                fieldWithPath("data.reviewDto.reviewInfo.commentNum").description("리뷰 댓글 수"),
                                fieldWithPath("data.reviewDto.reviewInfo.createdAt").description("리뷰 생성일"),
                                fieldWithPath("data.reviewDto.reviewInfo.averageRating").description("리뷰 평균 점수"),
                                fieldWithPath("data.userOtherReviews").description("유저 다른 빵집 리뷰 리스트"),
                                fieldWithPath("data.userOtherReviews.[].id").description("리뷰 고유 번호"),
                                fieldWithPath("data.userOtherReviews.[].image").description("리뷰 이미지"),
                                fieldWithPath("data.userOtherReviews.[].rating").description("리뷰 점수"),
                                fieldWithPath("data.bakeryOtherReviews").description("빵집 다른 리뷰 리스트"),
                                fieldWithPath("data.bakeryOtherReviews.[].id").description("리뷰 고유 번호"),
                                fieldWithPath("data.bakeryOtherReviews.[].image").description("리뷰 이미지"),
                                fieldWithPath("data.bakeryOtherReviews.[].rating").description("리뷰 점수")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
//    @Transactional
    void addReview() throws Exception {
        String object = objectMapper.writeValueAsString(ReviewRequest.builder()
                .productRatingList(Arrays.asList(
                        ReviewRequest.ProductRatingRequest.builder().productId(product1.getId()).rating(5L).build(),
                        ReviewRequest.ProductRatingRequest.builder().productId(product2.getId()).rating(4L).build()
                ))
                .noExistProductRatingRequestList(Arrays.asList(
                        ReviewRequest.NoExistProductRatingRequest.builder()
                                .productType(ProductType.BREAD).productName("fakeBread1").rating(5L).build(),
                        ReviewRequest.NoExistProductRatingRequest.builder()
                                .productType(ProductType.BREAD).productName("fakeBread2").rating(4L).build()
                )).content("review add test").build());

        mockMvc.perform(post("/review/{bakeryId}", bakery.getId())
                .content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("review/add",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(parameterWithName("bakeryId").description("빵집 고유 번호")),
                        requestFields(
                                fieldWithPath("productRatingList").description("리뷰 상품 점수 리스트"),
                                fieldWithPath("productRatingList.[].productId").description("리뷰 상품 고유 번호"),
                                fieldWithPath("productRatingList.[].rating").description("리뷰 상품 점수"),
                                fieldWithPath("noExistProductRatingRequestList").description("빵집에 없는 상품 점수 리스트"),
                                fieldWithPath("noExistProductRatingRequestList.[].productType").description("상품 타입"),
                                fieldWithPath("noExistProductRatingRequestList.[].productName").description("빵집에 없는 상품 이름"),
                                fieldWithPath("noExistProductRatingRequestList.[].rating").description("빵집에 없는 상품 점수"),
                                fieldWithPath("content").description("리뷰 내용")
                        ),
                        responseFields(
                                fieldWithPath("data.reviewId").description("리뷰 고유 번호")
                        )
                ))
                .andExpect(status().isCreated());
    }

    @Test
//    @Transactional
    void addReviewImage() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders
                .fileUpload("/review/{reviewId}/image", review1.getId())
                .file(new MockMultipartFile("files", UUID.randomUUID().toString() +".png", "image/png", "test".getBytes()))
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("review/add/image",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(parameterWithName("reviewId").description("리뷰 고유 번호")),
                        requestParts(
                                partWithName("files").description("리뷰 이미지들")
                        )
                ))
                .andExpect(status().isCreated());
    }

    @Test
//    @Transactional
    void addReviewTest() throws Exception {
        String object = objectMapper.writeValueAsString(ReviewRequest.builder()
                .productRatingList(Arrays.asList(
                        ReviewRequest.ProductRatingRequest.builder().productId(product1.getId()).rating(5L).build(),
                        ReviewRequest.ProductRatingRequest.builder().productId(product2.getId()).rating(4L).build()
                ))
                .noExistProductRatingRequestList(Arrays.asList(
                        ReviewRequest.NoExistProductRatingRequest.builder()
                                .productType(ProductType.BREAD).productName("fakeBread1").rating(5L).build(),
                        ReviewRequest.NoExistProductRatingRequest.builder()
                                .productType(ProductType.BREAD).productName("fakeBread2").rating(4L).build()
                )).content("review add test").build());
        MockMultipartFile request = new MockMultipartFile("request", "", "application/json", object.getBytes());

        mockMvc.perform(RestDocumentationRequestBuilders
                .fileUpload("/review/{bakeryId}/test", bakery.getId())
                .file(new MockMultipartFile("files", UUID.randomUUID().toString() +".png", "image/png", "test".getBytes()))
                .file(request).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("review/addTest",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(parameterWithName("bakeryId").description("빵집 고유 번호")),
                        requestParts(
                                partWithName("request").description("리뷰 정보"),
                                partWithName("files").description("리뷰 이미지들")
                        ),
                        requestPartFields("request",
                                fieldWithPath("productRatingList").description("리뷰 상품 점수 리스트"),
                                fieldWithPath("productRatingList.[].productId").description("리뷰 상품 고유 번호"),
                                fieldWithPath("productRatingList.[].rating").description("리뷰 상품 점수"),
                                fieldWithPath("noExistProductRatingRequestList").description("빵집에 없는 상품 점수 리스트"),
                                fieldWithPath("noExistProductRatingRequestList.[].productType").description("상품 타입"),
                                fieldWithPath("noExistProductRatingRequestList.[].productName").description("빵집에 없는 상품 이름"),
                                fieldWithPath("noExistProductRatingRequestList.[].rating").description("빵집에 없는 상품 점수"),
                                fieldWithPath("content").description("리뷰 내용")
                        )
                ))
                .andExpect(status().isCreated());
    }

    @Test
//    @Transactional
    void removeReview() throws Exception {
        mockMvc.perform(delete("/review/{reviewId}", review1.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("review/remove",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(parameterWithName("reviewId").description("리뷰 고유 번호"))
                ))
                .andExpect(status().isNoContent());
    }

    @Test
//    @Transactional
    void reviewLike() throws Exception {
        Review review = Review.builder().user(user).bakery(bakery).content("new content").build();
        reviewRepository.save(review);

        mockMvc.perform(post("/review/{reviewId}/like", review.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("review/like",
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
        mockMvc.perform(delete("/review/{reviewId}/unlike", review1.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("review/unlike",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(parameterWithName("reviewId").description("리뷰 고유 번호"))
                ))
                .andExpect(status().isNoContent());
    }

    @Test
//    @Transactional
    void getReviewCommentList() throws Exception {
        mockMvc.perform(get("/review/{reviewId}/comment", review1.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("review/comment/all",
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

        mockMvc.perform(post("/review/{reviewId}/comment", review1.getId())
                .content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("review/comment/add",
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
        mockMvc.perform(delete("/review/{reviewId}/comment/{commentId}", review1.getId(), comment1.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("review/comment/remove",
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
        mockMvc.perform(post("/review/{reviewId}/comment/{commentId}/like", review1.getId(), comment2.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("review/comment/like",
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
        mockMvc.perform(delete("/review/{reviewId}/comment/{commentId}/unlike", review1.getId(), comment1.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("review/comment/unlike",
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
    void reviewReport() throws Exception {
        String object = objectMapper.writeValueAsString(
                ReviewReportRequest.builder().reason(ReviewReportReason.COPYRIGHT_THEFT).content("Copyright").build());

        mockMvc.perform(post("/review/{reviewId}/report", review1.getId())
                .header("Authorization", "Bearer " + token.getAccessToken())
                .content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("review/report",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(parameterWithName("reviewId").description("리뷰 고유 번호")),
                        requestFields(
                                fieldWithPath("reason").description("리뷰 신고 이유"),
                                fieldWithPath("content").description("리뷰 신고 내용")
                        )
                ))
                .andExpect(status().isCreated());
    }
}