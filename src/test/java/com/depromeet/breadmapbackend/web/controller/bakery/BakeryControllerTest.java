package com.depromeet.breadmapbackend.web.controller.bakery;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.bakery.BakeryUpdateReason;
import com.depromeet.breadmapbackend.domain.product.Product;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.depromeet.breadmapbackend.domain.product.ProductType;
import com.depromeet.breadmapbackend.domain.review.*;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.utils.ControllerTest;
import com.depromeet.breadmapbackend.security.domain.RoleType;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.BakeryReportRequest;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.BakeryUpdateRequest;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.ProductReportRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BakeryControllerTest extends ControllerTest {
    private User user;
    private Bakery bakery1;
    private Bakery bakery2;
    private JwtToken token;

    @BeforeEach
    public void setup() {
        user = User.builder().nickName("nickname").roleType(RoleType.USER).username("username").build();
        userRepository.save(user);
        token = jwtTokenProvider.createJwtToken(user.getUsername(), user.getRoleType().getCode());

        List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);
        bakery1 = Bakery.builder().id(1L).address("address1").latitude(37.5596080725671).longitude(127.044235133983)
                .facilityInfoList(facilityInfo).name("bakery1").status(BakeryStatus.POSTING).build();
        bakery2 = Bakery.builder().id(2L).address("address2").latitude(37.55950448505721).longitude(127.04416263787213)
                .facilityInfoList(facilityInfo).name("bakery2").status(BakeryStatus.POSTING).build();

        bakeryRepository.save(bakery1);
        bakeryRepository.save(bakery2);

        Product product1 = Product.builder().bakery(bakery1).productType(ProductType.BREAD).name("bread1").price("3000").build();
        Product product2 = Product.builder().bakery(bakery2).productType(ProductType.BREAD).name("bread2").price("4000").build();
        productRepository.save(product1);
        productRepository.save(product2);

        Review review1 = Review.builder().user(user).bakery(bakery1).content("content1").build();
        Review review2 = Review.builder().user(user).bakery(bakery2).content("content1").build();
        reviewRepository.save(review1);
        reviewRepository.save(review2);

        ReviewProductRating rating1 = ReviewProductRating.builder().bakery(bakery1).product(product1).review(review1).rating(4L).build();
        ReviewProductRating rating2 = ReviewProductRating.builder().bakery(bakery1).product(product2).review(review2).rating(4L).build();
        reviewProductRatingRepository.save(rating1);
        reviewProductRatingRepository.save(rating2);

        ReviewLike reviewLike = ReviewLike.builder().review(review1).user(user).build();
        reviewLikeRepository.save(reviewLike);

        ReviewComment comment1 = ReviewComment.builder().review(review1).user(user).content("comment1").build();
        ReviewComment comment2 = ReviewComment.builder().review(review1).user(user).content("comment1").parent(comment1).build();
        reviewCommentRepository.save(comment1);
        reviewCommentRepository.save(comment2);

        ReviewCommentLike reviewCommentLike = ReviewCommentLike.builder().reviewComment(comment1).user(user).build();
        reviewCommentLikeRepository.save(reviewCommentLike);
    }

    @AfterEach
    public void setDown() {
        bakeryUpdateReportImageRepository.deleteAllInBatch();
        bakeryUpdateReportRepository.deleteAllInBatch();
        bakeryAddReportRepository.deleteAllInBatch();
        bakeryReportImageRepository.deleteAllInBatch();
        productAddReportImageRepository.deleteAllInBatch();
        productAddReportRepository.deleteAllInBatch();
        flagBakeryRepository.deleteAllInBatch();
        flagRepository.deleteAllInBatch();
        reviewProductRatingRepository.deleteAllInBatch();
        reviewCommentLikeRepository.deleteAllInBatch();
        reviewCommentRepository.deleteAllInBatch();
        reviewLikeRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        bakeryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
//    @Transactional
    void findBakeryList() throws Exception {
        mockMvc.perform(get("/bakery?sortBy=distance&latitude=37.560992&longitude=127.044174&latitudeDelta=0.01&longitudeDelta=0.02")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("bakery/find/default",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        requestParameters(
                                parameterWithName("sortBy").description("정렬 방법 (distance, popular)"),
                                parameterWithName("latitude").description("중앙 위도"),
                                parameterWithName("longitude").description("중앙 경도"),
                                parameterWithName("latitudeDelta").description("위도 범위"),
                                parameterWithName("longitudeDelta").description("경도 범위")
                        ),
                        responseFields(
                                fieldWithPath("data.[].latitude").description("빵집 위도"),
                                fieldWithPath("data.[].longitude").description("빵집 경도"),
                                fieldWithPath("data.[].image").description("빵집 이미지"),
                                fieldWithPath("data.[].id").description("빵집 고유 번호"),
                                fieldWithPath("data.[].name").description("빵집 이름"),
                                fieldWithPath("data.[].flagNum").description("빵집 가봤어요 수"),
                                fieldWithPath("data.[].rating").description("빵집 평점"),
                                fieldWithPath("data.[].reviewNum").description("빵집 리뷰 수"),
                                fieldWithPath("data.[].simpleReviewList").description("빵집 리뷰 리스트"),
                                fieldWithPath("data.[].simpleReviewList.[].id").description("빵집 리뷰 아이디"),
                                fieldWithPath("data.[].simpleReviewList.[].content").description("빵집 리뷰 내용"),
                                fieldWithPath("data.[].distance").description("빵집까지 거리"),
                                fieldWithPath("data.[].popularNum").description("빵집 인기수"),
                                fieldWithPath("data.[].color").description("빵집 깃발 색깔 (오렌지 색)")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
//    @Transactional
    void findBakeryListByFilter() throws Exception {
        Flag flag = Flag.builder().user(user).name("testFlagName").color(FlagColor.ORANGE).build();
        flagRepository.save(flag);

        FlagBakery flagBakery = FlagBakery.builder().flag(flag).bakery(bakery1).user(user).build();
        flagBakeryRepository.save(flagBakery);

        mockMvc.perform(get("/bakery/filter?sortBy=distance&latitude=37.560992&longitude=127.044174&latitudeDelta=0.01&longitudeDelta=0.02")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("bakery/find/filter",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        requestParameters(
                                parameterWithName("sortBy").description("정렬 방법 (distance, popular)"),
                                parameterWithName("latitude").description("중앙 위도"),
                                parameterWithName("longitude").description("중앙 경도"),
                                parameterWithName("latitudeDelta").description("위도 범위"),
                                parameterWithName("longitudeDelta").description("경도 범위")
                        ),
                        responseFields(
                                fieldWithPath("data.[].latitude").description("빵집 위도"),
                                fieldWithPath("data.[].longitude").description("빵집 경도"),
                                fieldWithPath("data.[].image").description("빵집 이미지"),
                                fieldWithPath("data.[].id").description("빵집 고유 번호"),
                                fieldWithPath("data.[].name").description("빵집 이름"),
                                fieldWithPath("data.[].flagNum").description("빵집 가봤어요 수"),
                                fieldWithPath("data.[].rating").description("빵집 평점"),
                                fieldWithPath("data.[].reviewNum").description("빵집 리뷰 수"),
                                fieldWithPath("data.[].simpleReviewList").description("빵집 리뷰 리스트"),
                                fieldWithPath("data.[].simpleReviewList.[].id").description("빵집 리뷰 아이디"),
                                fieldWithPath("data.[].simpleReviewList.[].content").description("빵집 리뷰 내용"),
                                fieldWithPath("data.[].distance").description("빵집까지 거리"),
                                fieldWithPath("data.[].popularNum").description("빵집 인기수"),
                                fieldWithPath("data.[].color").description("빵집 깃발 색깔 (지정하지 않은 경우 회색)")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
//    @Transactional
    void findBakery() throws Exception {
        mockMvc.perform(get("/bakery/{bakeryId}", bakery1.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("bakery/find",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호")
                        ),
                        responseFields(
                                fieldWithPath("data.bakeryInfo").description("빵집 정보"),
                                fieldWithPath("data.bakeryInfo.image").description("빵집 이미지"),
                                fieldWithPath("data.bakeryInfo.name").description("빵집 이름"),
                                fieldWithPath("data.bakeryInfo.flagNum").description("빵집 가봤어요 수"),
                                fieldWithPath("data.bakeryInfo.rating").description("빵집 평점"),
                                fieldWithPath("data.bakeryInfo.reviewNum").description("빵집 리뷰 수"),
                                fieldWithPath("data.bakeryInfo.address").description("빵집 도로명 주소"),
                                fieldWithPath("data.bakeryInfo.hours").description("빵집 영업 시간"),
                                fieldWithPath("data.bakeryInfo.websiteURL").description("빵집 홈페이지"),
                                fieldWithPath("data.bakeryInfo.instagramURL").description("빵집 인스타그램"),
                                fieldWithPath("data.bakeryInfo.facebookURL").description("빵집 페이스북"),
                                fieldWithPath("data.bakeryInfo.blogURL").description("빵집 블로그"),
                                fieldWithPath("data.bakeryInfo.phoneNumber").description("빵집 전화번호"),
                                fieldWithPath("data.flagInfo.flagId").description("유저 빵집 깃발 고유 번호 (깃발 미선택 시 null)"),
                                fieldWithPath("data.flagInfo.isFlaged").description("유저 빵집 깃발 저장 유무 (깃발 미선택 시 false)"),
                                fieldWithPath("data.facilityInfoList")
                                        .description("빵집 시설 정보 (PARKING(\"주차 가능\"),\n" +
                                                "WIFI(\"와이파이\"),\n" +
                                                "DELIVERY(\"배달\"),\n" +
                                                "PET(\"반려동물\"),\n" +
                                                "SHIPPING(\"택배\"),\n" +
                                                "BOOKING(\"예약\"))")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void findProductList() throws Exception {
        mockMvc.perform(get("/bakery/{bakeryId}/product", bakery1.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("bakery/product",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호")
                        ),
                        responseFields(
                                fieldWithPath("data.[].id").description("상품 고유번호"),
                                fieldWithPath("data.[].name").description("상품 이름"),
                                fieldWithPath("data.[].rating").description("상품 평점"),
                                fieldWithPath("data.[].reviewNum").description("상품 리뷰 수"),
                                fieldWithPath("data.[].price").description("상품 가격"),
                                fieldWithPath("data.[].image").description("상품 이미지")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void bakeryUpdateReport() throws Exception {
        String object = objectMapper.writeValueAsString(BakeryUpdateRequest.builder()
                .reason(BakeryUpdateReason.BAKERY_SHUTDOWN).content("newContent").build());
        MockMultipartFile request =
                new MockMultipartFile("request", "", "application/json", object.getBytes());

        mockMvc.perform(RestDocumentationRequestBuilders
                        .fileUpload("/bakery/report/{bakeryId}/update", bakery1.getId())
                        .file(new MockMultipartFile("files", UUID.randomUUID() +".png", "image/png", "test".getBytes()))
                        .file(request).accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("bakery/report/update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(parameterWithName("bakeryId").description("빵집 고유 번호")),
                        requestParts(
                                partWithName("request").description("빵집 수정 제보"),
                                partWithName("files").description("빵집 수정 이미지들")
                        ),
                        requestPartBody("request"),
                        requestPartFields("request",
                                fieldWithPath("reason").description("빵집 수정 제보 이유"),
                                fieldWithPath("content").optional().description("수정 사항")
                        )
                ))
                .andExpect(status().isCreated());
    }

    @Test
    void bakeryAddReport() throws Exception {
        String object = objectMapper.writeValueAsString(BakeryReportRequest.builder()
                .name("newBakery").location("newLocation").content("newContent").build());

        mockMvc.perform(post("/bakery/report/add")
                .header("Authorization", "Bearer " + token.getAccessToken())
                .content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("bakery/report/add",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        requestFields(
                                fieldWithPath("name").description("제보 빵집 이름"),
                                fieldWithPath("location").description("제보 빵집 위치"),
                                fieldWithPath("content").optional().description("추천 이유")
                        )
                ))
                .andExpect(status().isCreated());
    }

    @Test
    void bakeryReportImage() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders
                        .fileUpload("/bakery/report/{bakeryId}/image", bakery1.getId())
                        .file(new MockMultipartFile("files", UUID.randomUUID() +".png", "image/png", "test".getBytes()))
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("bakery/report/image",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(parameterWithName("bakeryId").description("빵집 고유 번호"))
                ))
                .andExpect(status().isCreated());
    }

    @Test
    void productAddReport() throws Exception {
        String object = objectMapper.writeValueAsString(ProductReportRequest.builder().name("newBread").price("4000").build());
        MockMultipartFile request =
                new MockMultipartFile("request", "", "application/json", object.getBytes());

        mockMvc.perform(RestDocumentationRequestBuilders
                .fileUpload("/bakery/report/{bakeryId}/product", bakery1.getId())
                .file(new MockMultipartFile("files", UUID.randomUUID() +".png", "image/png", "test".getBytes()))
                .file(request).accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("bakery/report/product",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(parameterWithName("bakeryId").description("빵집 고유 번호")),
                        requestParts(
                                partWithName("request").description("제보 상품 정보"),
                                partWithName("files").description("제보 상품 이미지들")
                        ),
                        requestPartBody("request"),
                        requestPartFields("request",
                                fieldWithPath("name").description("제보 상품 이름"),
                                fieldWithPath("price").description("제보 상품 가격")
                        )
                ))
                .andExpect(status().isCreated());
    }

    @Test
    void searchSimpleProductList() throws Exception {
        mockMvc.perform(get("/bakery/{bakeryId}/review/product/search?name=bread", bakery1.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("bakery/review/product/search",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(parameterWithName("bakeryId").description("빵집 고유 번호")),
                        requestParameters(parameterWithName("name").description("검색 키워드")),
                        responseFields(
                                fieldWithPath("data.[].id").description("상품 고유 번호"),
                                fieldWithPath("data.[].name").description("상품 이름"),
                                fieldWithPath("data.[].price").description("상품 가격"),
                                fieldWithPath("data.[].image").description("상품 이미지")
                        )
                ))
                .andExpect(status().isOk());
    }
}