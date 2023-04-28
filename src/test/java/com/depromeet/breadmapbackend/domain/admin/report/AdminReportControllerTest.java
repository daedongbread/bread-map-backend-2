package com.depromeet.breadmapbackend.domain.admin.report;

import com.depromeet.breadmapbackend.domain.admin.Admin;
import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.bakery.product.Product;
import com.depromeet.breadmapbackend.domain.bakery.product.ProductType;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewImage;
import com.depromeet.breadmapbackend.domain.review.ReviewProductRating;
import com.depromeet.breadmapbackend.domain.review.report.ReviewReport;
import com.depromeet.breadmapbackend.domain.review.report.ReviewReportReason;
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

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminReportControllerTest extends ControllerTest {
    private JwtToken token;
    private ReviewReport reviewReport;

    @BeforeEach
    public void setup() throws IOException {
        Admin admin = Admin.builder().email("email").password(passwordEncoder.encode("password")).build();
        adminRepository.save(admin);
        token = jwtTokenProvider.createJwtToken(admin.getEmail(), admin.getRoleType().getCode());

        User user = User.builder().oAuthInfo(OAuthInfo.builder().oAuthType(OAuthType.GOOGLE).oAuthId("oAuthId1").build())
                .userInfo(UserInfo.builder().nickName("nickname1").build()).build();
        userRepository.save(user);

        List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);
        Bakery bakery = Bakery.builder().address("address").latitude(37.5596080725671).longitude(127.044235133983)
                .facilityInfoList(facilityInfo).name("bakery").status(BakeryStatus.POSTING).build();
        bakeryRepository.save(bakery);

        Product product = Product.builder().bakery(bakery).productType(ProductType.BREAD).name("bread1").price("3000").build();
        productRepository.save(product);

        Review review = Review.builder().user(user).bakery(bakery).content("content1").build();
        reviewRepository.save(review);
        ReviewImage image = ReviewImage.builder().review(review).bakery(bakery).image("reviewImage.jpg").build();
        reviewImageRepository.save(image);
        ReviewProductRating rating = ReviewProductRating.builder().user(user).bakery(bakery).product(product).review(review).rating(4L).build();
        reviewProductRatingRepository.save(rating);

        reviewReport = ReviewReport.builder()
                .reporter(user).review(review).reason(ReviewReportReason.COPYRIGHT_THEFT).content("content").build();
        reviewReportRepository.save(reviewReport);
    }

    @AfterEach
    public void setDown() {
        reviewProductRatingRepository.deleteAllInBatch();
        reviewReportRepository.deleteAllInBatch();
        reviewImageRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        bakeryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        adminRepository.deleteAllInBatch();
    }

    @Test
    void getReviewReportList() throws Exception {
        mockMvc.perform(get("/v1/admin/review-reports?page=0")
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/admin/reviewReport/all",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호")),
                        responseFields(
                                fieldWithPath("data.pageNumber").description("현재 페이지 (0부터 시작)"),
                                fieldWithPath("data.numberOfElements").description("현재 페이지 데이터 수"),
                                fieldWithPath("data.size").description("페이지 크기"),
                                fieldWithPath("data.totalElements").description("전체 데이터 수"),
                                fieldWithPath("data.totalPages").description("전체 페이지 수"),
                                fieldWithPath("data.contents").description("리뷰 신고 리스트"),
                                fieldWithPath("data.contents.[].reviewReportId").description("리뷰 신고 고유 번호"),
                                fieldWithPath("data.contents.[].reporterNickName").description("신고자 닉네임"),
                                fieldWithPath("data.contents.[].reason")
                                        .description("리뷰 신고 이유 (" +
                                                "IRRELEVANT_CONTENT(\"리뷰와 관계없는 내용\"),\n" +
                                                "INAPPROPRIATE_CONTENT(\"음란성, 욕설 등 부적절한 내용\"),\n" +
                                                "IRRELEVANT_IMAGE(\"리뷰와 관련없는 사진 게시\"),\n" +
                                                "UNFIT_CONTENT(\"리뷰 작성 취지에 맞지 않는 내용(복사글 등)\"),\n" +
                                                "COPYRIGHT_THEFT(\"저작권 도용 의심(사진 등)\"),\n" +
                                                "ETC(\"기타(하단 내용 작성)\"))"),
                                fieldWithPath("data.contents.[].respondentNickName").description("피신고자 닉네임"),
                                fieldWithPath("data.contents.[].reportedReviewId").description("신고된 리뷰 고유 번호"),
                                fieldWithPath("data.contents.[].content").description("리뷰 신고 내용"),
                                fieldWithPath("data.contents.[].createdAt").description("리뷰 신고 시간"),
                                fieldWithPath("data.contents.[].block").description("리뷰 차단 여부")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void updateReviewInUse() throws Exception {
        mockMvc.perform(patch("/v1/admin/review-reports/{reportId}", reviewReport.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/admin/reviewReport/update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(
                                parameterWithName("reportId").description("리뷰 신고 고유 번호"))
                ))
                .andExpect(status().isNoContent());
    }
}
