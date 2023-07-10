package com.depromeet.breadmapbackend.domain.bakery;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.bakery.product.Product;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.bakery.view.BakeryView;
import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.depromeet.breadmapbackend.domain.bakery.product.ProductType;
import com.depromeet.breadmapbackend.domain.review.*;
import com.depromeet.breadmapbackend.domain.review.comment.ReviewComment;
import com.depromeet.breadmapbackend.domain.review.comment.like.ReviewCommentLike;
import com.depromeet.breadmapbackend.domain.review.like.ReviewLike;
import com.depromeet.breadmapbackend.domain.user.OAuthInfo;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserInfo;
import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import com.depromeet.breadmapbackend.utils.ControllerTest;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.domain.bakery.report.dto.BakeryAddReportRequest;
import com.depromeet.breadmapbackend.domain.bakery.report.dto.BakeryUpdateReportRequest;
import com.depromeet.breadmapbackend.domain.bakery.product.dto.ProductReportRequest;
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
        user = User.builder().oAuthInfo(OAuthInfo.builder().oAuthType(OAuthType.GOOGLE).oAuthId("oAuthId").build())
                .userInfo(UserInfo.builder().nickName("nickname").build()).build();
        userRepository.save(user);
        token = jwtTokenProvider.createJwtToken(user.getOAuthId(), user.getRoleType().getCode());

        List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);
        bakery1 = Bakery.builder().address("address1").latitude(37.5596080725671).longitude(127.044235133983)
                .facilityInfoList(facilityInfo).name("bakery1").status(BakeryStatus.POSTING).build();
        bakeryRepository.save(bakery1);
        // bakeryViewRepository.save(BakeryView.builder().bakery(bakery1).build());
        bakery2 = Bakery.builder().address("address2").latitude(37.55950448505721).longitude(127.04416263787213)
                .facilityInfoList(facilityInfo).name("bakery2").status(BakeryStatus.POSTING).build();
        bakeryRepository.save(bakery2);

        Product product1 = Product.builder().bakery(bakery1).productType(ProductType.BREAD).name("bread1").price("3000").build();
        Product product2 = Product.builder().bakery(bakery2).productType(ProductType.BREAD).name("bread2").price("4000").build();
        productRepository.save(product1);
        productRepository.save(product2);

        Review review1 = Review.builder().user(user).bakery(bakery1).content("content1").build();
        Review review2 = Review.builder().user(user).bakery(bakery2).content("content1").build();
        reviewRepository.save(review1);
        reviewRepository.save(review2);

        ReviewProductRating rating1 = ReviewProductRating.builder().user(user).bakery(bakery1).product(product1).review(review1).rating(4L).build();
        ReviewProductRating rating2 = ReviewProductRating.builder().user(user).bakery(bakery1).product(product2).review(review2).rating(4L).build();
        reviewProductRatingRepository.save(rating1);
        reviewProductRatingRepository.save(rating2);
    }

    @AfterEach
    public void setDown() {
        reviewProductRatingRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        bakeryViewRepository.deleteAllInBatch();
        bakeryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
//    @Transactional
    void getBakeryList() throws Exception {
        mockMvc.perform(get("/v1/bakeries?sortBy=distance&filterBy=false&latitude=37.560992&longitude=127.044174&latitudeDelta=0.01&longitudeDelta=0.02")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/bakery/find/default",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        requestParameters(
                                parameterWithName("sortBy").description("정렬 방법 (distance, popular)"),
                                parameterWithName("filterBy").description("필터 여부 (true, false(디폴트))"),
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
                                fieldWithPath("data.[].color").description("빵집 깃발 색깔 " +
                                        "(기본 오렌지 색, 필터 적용 시 깃발에 추가하지 않은 빵집은 회색)")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
//    @Transactional
    void getBakery() throws Exception {
        mockMvc.perform(get("/v1/bakeries/{bakeryId}", bakery1.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/bakery/find",
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
                                fieldWithPath("data.flagInfo").description("빵집 깃발 정보"),
                                fieldWithPath("data.flagInfo.flagId").description("유저 빵집 깃발 고유 번호 (깃발 미선택 시 null)"),
                                fieldWithPath("data.flagInfo.isFlaged").description("유저 빵집 깃발 저장 유무 (깃발 미선택 시 false)"),
                                fieldWithPath("data.facilityInfoList")
                                        .description("빵집 시설 정보 (PARKING(\"주차 가능\"),\n" +
                                                "WIFI(\"와이파이\"),\n" +
                                                "DELIVERY(\"배달\"),\n" +
                                                "PET(\"반려동물\"),\n" +
                                                "SHIPPING(\"택배\"),\n" +
                                                "BOOKING(\"예약\"))"),
                                fieldWithPath("data.pioneerInfo").description("빵집 개척자 정보"),
                                fieldWithPath("data.pioneerInfo.pioneerId").description("빵집 개척자 고유 번호"),
                                fieldWithPath("data.pioneerInfo.pioneerNickName").description("빵집 개척자 닉네임")
                        )
                ))
                .andExpect(status().isOk());
    }
}