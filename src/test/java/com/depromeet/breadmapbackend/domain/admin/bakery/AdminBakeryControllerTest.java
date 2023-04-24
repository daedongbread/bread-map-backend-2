package com.depromeet.breadmapbackend.domain.admin.bakery;

import com.depromeet.breadmapbackend.domain.admin.Admin;
import com.depromeet.breadmapbackend.domain.admin.bakery.dto.BakeryAddRequest;
import com.depromeet.breadmapbackend.domain.admin.bakery.dto.BakeryUpdateRequest;
import com.depromeet.breadmapbackend.domain.admin.bakery.dto.ProductAddImageRegisterRequest;
import com.depromeet.breadmapbackend.domain.admin.bakery.param.AdminBakeryImageType;
import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.bakery.product.Product;
import com.depromeet.breadmapbackend.domain.bakery.product.ProductType;
import com.depromeet.breadmapbackend.domain.bakery.product.report.ProductAddReport;
import com.depromeet.breadmapbackend.domain.bakery.product.report.ProductAddReportImage;
import com.depromeet.breadmapbackend.domain.bakery.report.*;
import com.depromeet.breadmapbackend.domain.bakery.view.BakeryView;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewImage;
import com.depromeet.breadmapbackend.domain.review.ReviewProductRating;
import com.depromeet.breadmapbackend.domain.review.report.ReviewReport;
import com.depromeet.breadmapbackend.domain.review.report.ReviewReportReason;
import com.depromeet.breadmapbackend.domain.review.view.ReviewView;
import com.depromeet.breadmapbackend.domain.user.OAuthInfo;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserInfo;
import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.utils.ControllerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Arrays;
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
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminBakeryControllerTest extends ControllerTest {
    private Bakery bakery;
    private Product product;
    private BakeryUpdateReport bakeryUpdateReport;
    private ProductAddReport productAddReport;
    private ProductAddReportImage productAddReportImage1;
    private ProductAddReportImage productAddReportImage2;
    private BakeryReportImage bakeryReportImage;
    private JwtToken token;

    @BeforeEach
    public void setup() throws IOException {
        Admin admin = Admin.builder().email("email").password(passwordEncoder.encode("password")).build();
        adminRepository.save(admin);
        token = jwtTokenProvider.createJwtToken(admin.getEmail(), admin.getRoleType().getCode());

        User user = User.builder().oAuthInfo(OAuthInfo.builder().oAuthType(OAuthType.GOOGLE).oAuthId("oAuthId1").build())
                .userInfo(UserInfo.builder().nickName("nickname1").build()).build();
        userRepository.save(user);

        List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);
        bakery = Bakery.builder().address("address").latitude(37.5596080725671).longitude(127.044235133983)
                .facilityInfoList(facilityInfo).name("bakery").status(BakeryStatus.POSTING)
                .image(customAWSS3Properties.getCloudFront() + "/" + "bakeryImage.jpg").build();
        bakeryRepository.save(bakery);
        s3Uploader.upload(
                new MockMultipartFile("image", "bakeryImage.jpg", "image/jpg", "test".getBytes()),
                "bakeryImage.jpg");
        bakeryViewRepository.save(BakeryView.builder().bakery(bakery).build());

        product = Product.builder()
                .bakery(bakery).productType(ProductType.BREAD).name("bread1").price("3000").image(customAWSS3Properties.getCloudFront() + "/" + "productImage.jpg").build();
        productRepository.save(product);

        bakeryUpdateReport = BakeryUpdateReport.builder()
                .bakery(bakery).user(user).reason(BakeryUpdateReason.BAKERY_SHUTDOWN).content("content").build();
        bakeryUpdateReportRepository.save(bakeryUpdateReport);
        BakeryUpdateReportImage bakeryUpdateReportImage = BakeryUpdateReportImage.builder().bakery(bakery).report(bakeryUpdateReport).image("image").build();
        bakeryUpdateReportImageRepository.save(bakeryUpdateReportImage);

        bakeryReportImage = BakeryReportImage.builder().bakery(bakery).image("bakeryReportImage.jpg").user(user).build();
        bakeryReportImageRepository.save(bakeryReportImage);

        productAddReport = ProductAddReport.builder().bakery(bakery).user(user).name("newBread").price("1000").build();
        productAddReportRepository.save(productAddReport);
        productAddReportImage1 = ProductAddReportImage.builder()
                .productAddReport(productAddReport).image(customAWSS3Properties.getCloudFront() + "/productImage1.jpg").build();
        productAddReportImageRepository.save(productAddReportImage1);
        productAddReportImage2 = ProductAddReportImage.builder()
                .productAddReport(productAddReport).image(customAWSS3Properties.getCloudFront() + "/productImage2.jpg").build();
        productAddReportImageRepository.save(productAddReportImage2);

        Review review = Review.builder().user(user).bakery(bakery).content("content1").build();
        reviewRepository.save(review);
        reviewViewRepository.save(ReviewView.builder().review(review).build());
        ReviewImage image = ReviewImage.builder().review(review).bakery(bakery).image("reviewImage.jpg").build();
        reviewImageRepository.save(image);
        ReviewProductRating rating = ReviewProductRating.builder().user(user).bakery(bakery).product(product).review(review).rating(4L).build();
        reviewProductRatingRepository.save(rating);

        ReviewReport reviewReport = ReviewReport.builder()
                .reporter(user).review(review).reason(ReviewReportReason.COPYRIGHT_THEFT).content("content").build();
        reviewReportRepository.save(reviewReport);

        s3Uploader.upload(
                new MockMultipartFile("image", "tempImage.jpg", "image/jpg", "test".getBytes()),
                "tempImage.jpg");
    }

    @AfterEach
    public void setDown() {
        s3Uploader.deleteFileS3(customAWSS3Properties.getBucket() + "/tempImage.jpg");
        s3Uploader.deleteFileS3(customAWSS3Properties.getBucket() + "/bakeryImage.jpg");
        bakeryUpdateReportImageRepository.deleteAllInBatch();
        bakeryUpdateReportRepository.deleteAllInBatch();
        bakeryReportImageRepository.deleteAllInBatch();
        productAddReportImageRepository.deleteAllInBatch();
        productAddReportRepository.deleteAllInBatch();
        reviewProductRatingRepository.deleteAllInBatch();
        reviewReportRepository.deleteAllInBatch();
        reviewImageRepository.deleteAllInBatch();
        reviewViewRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        bakeryViewRepository.deleteAllInBatch();
        bakeryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        adminRepository.deleteAllInBatch();
    }

    @Test
    void getBakeryAlarmBar() throws Exception {
        mockMvc.perform(get("/v1/admin/bakeries/alarm-bar")
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/admin/bakery/alarm-bar",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        responseFields(
                                fieldWithPath("data.newAlarmNum").description("미확인 알람 갯수")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void getBakeryList() throws Exception {
        mockMvc.perform(get("/v1/admin/bakeries?page=0")
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/admin/bakery/all",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        requestParameters(
                                parameterWithName("filterBy").optional()
                                        .description("빵집 필터 (없으면 전체 조회, 필터 여러 개 가능) " +
                                                "(bakery_report_image (대표 이미지), product_add_report (메뉴 제보), " +
                                                "bakery_update_report (정보 수정), new_review(신규 리뷰))"),
                                parameterWithName("name").optional().description("검색어"),
                                parameterWithName("page").description("페이지 번호")),
                        responseFields(
                                fieldWithPath("data.pageNumber").description("현재 페이지 (0부터 시작)"),
                                fieldWithPath("data.numberOfElements").description("현재 페이지 데이터 수"),
                                fieldWithPath("data.size").description("페이지 크기"),
                                fieldWithPath("data.totalElements").description("전체 데이터 수"),
                                fieldWithPath("data.totalPages").description("전체 페이지 수"),
                                fieldWithPath("data.contents").description("빵집 리스트"),
                                fieldWithPath("data.contents.[].bakeryId").description("빵집 고유 번호"),
                                fieldWithPath("data.contents.[].name").description("빵집 이름"),
                                fieldWithPath("data.contents.[].bakeryReportImageNum").description("미확인 대표 이미지 갯수"),
                                fieldWithPath("data.contents.[].productAddReportNum").description("미확인 메뉴 제보 갯수"),
                                fieldWithPath("data.contents.[].bakeryUpdateReportNum").description("미확인 정보 수정 갯수"),
                                fieldWithPath("data.contents.[].newReviewNum").description("미확인 신규 리뷰 갯수"),
                                fieldWithPath("data.contents.[].createdAt").description("빵집 최초 등록일"),
                                fieldWithPath("data.contents.[].modifiedAt").description("빵집 마지막 수정일"),
                                fieldWithPath("data.contents.[].status")
                                        .description("빵집 게시상태 (" +
                                                "POSTING(\"게시중\"),\n" +
                                                "UNPOSTING(\"미게시\"))")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void getBakery() throws Exception {
        mockMvc.perform(get("/v1/admin/bakeries/{bakeryId}", bakery.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/admin/bakery",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호")),
                        responseFields(
                                fieldWithPath("data.name").description("빵집 이름"),
                                fieldWithPath("data.image").description("빵집 이미지"),
                                fieldWithPath("data.address").description("빵집 도로명 주소"),
                                fieldWithPath("data.latitude").description("빵집 위도"),
                                fieldWithPath("data.longitude").description("빵집 경도"),
                                fieldWithPath("data.hours").description("빵집 영업 시간"),
                                fieldWithPath("data.websiteURL").description("빵집 홈페이지"),
                                fieldWithPath("data.instagramURL").description("빵집 인스타그램"),
                                fieldWithPath("data.facebookURL").description("빵집 페이스북"),
                                fieldWithPath("data.blogURL").description("빵집 블로그"),
                                fieldWithPath("data.phoneNumber").description("빵집 전화번호"),
                                fieldWithPath("data.facilityInfoList")
                                        .description("빵집 시설 정보 (PARKING(\"주차가능\"),\n" +
                                                "WIFI(\"와이파이\"),\n" +
                                                "DELIVERY(\"배달\"),\n" +
                                                "PET(\"반려동물\"),\n" +
                                                "SHIPPING(\"택배\"),\n" +
                                                "BOOKING(\"예약\"))"),
                                fieldWithPath("data.status").description("빵집 게시 상태 (" +
                                        "POSTING(\"게시중\"), UNPOSTING(\"미게시\"))"),
                                fieldWithPath("data.productList").description("빵집 메뉴"),
                                fieldWithPath("data.productList.[].productId").description("상품 고유 번호"),
                                fieldWithPath("data.productList.[].productType").description("상품 타입"),
                                fieldWithPath("data.productList.[].productName").description("상품 이름"),
                                fieldWithPath("data.productList.[].price").description("상품 가격"),
                                fieldWithPath("data.productList.[].image").description("상품 이미지")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void getBakeryLatitudeLongitude() throws Exception {
        mockMvc.perform(get("/v1/admin/bakeries/location?address=서울 중구 세종대로 110 서울특별시청")
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/admin/bakery/location",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        requestParameters(
                                parameterWithName("address").description("조회 주소")),
                        responseFields(
                                fieldWithPath("data.latitude").description("빵집 위도"),
                                fieldWithPath("data.longitude").description("빵집 경도")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void addBakery() throws Exception {
        List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);
        String object = objectMapper.writeValueAsString(BakeryAddRequest.builder()
                .name("newBakery").image("tempImage.jpg")
                .address("address").latitude(35.124124).longitude(127.312452).hours("09:00~20:00")
                .instagramURL("insta").facebookURL("facebook").blogURL("blog").websiteURL("website").phoneNumber("010-1234-5678")
                .facilityInfoList(facilityInfo).status(BakeryStatus.POSTING).productList(Arrays.asList(
                        BakeryAddRequest.ProductAddRequest.builder()
                                .productType(ProductType.BREAD).productName("testBread").price("12000")
                                .image("tempImage.jpg").build()
                )).build());

        mockMvc.perform(post("/v1/admin/bakeries")
                        .content(object).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/admin/bakery/add",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        requestFields(
                                fieldWithPath("name").description("빵집 이름"),
                                fieldWithPath("image").optional().description("빵집 이미지"),
                                fieldWithPath("address").description("빵집 도로명 주소"),
                                fieldWithPath("latitude").description("빵집 위도"),
                                fieldWithPath("longitude").description("빵집 경도"),
                                fieldWithPath("hours").optional().description("빵집 영업시간"),
                                fieldWithPath("instagramURL").optional().description("빵집 인스타그램"),
                                fieldWithPath("facebookURL").optional().description("빵집 페이스북"),
                                fieldWithPath("blogURL").optional().description("빵집 블로그"),
                                fieldWithPath("websiteURL").optional().description("빵집 홈페이지"),
                                fieldWithPath("phoneNumber").optional().description("빵집 전화번호"),
                                fieldWithPath("facilityInfoList.[]").optional()
                                        .description("빵집 시설 정보 (PARKING(\"주차가능\"),\n" +
                                                "WIFI(\"와이파이\"),\n" +
                                                "DELIVERY(\"배달\"),\n" +
                                                "PET(\"반려동물\"),\n" +
                                                "SHIPPING(\"택배\"),\n" +
                                                "BOOKING(\"예약\"))"),
                                fieldWithPath("productList").optional().description("상품 리스트"),
                                fieldWithPath("productList.[].productType").description("상품 타입 (BREAD, BEVERAGE, ETC)"),
                                fieldWithPath("productList.[].productName").description("상품 이름"),
                                fieldWithPath("productList.[].price").description("상품 가격"),
                                fieldWithPath("productList.[].image").optional().description("상품 이미지"),
                                fieldWithPath("status").description("빵집 게시 상태 (" +
                                        "POSTING(\"게시중\"), UNPOSTING(\"미게시\"))")
                        )
                ))
                .andExpect(status().isCreated());
    }

    @Test
    void updateBakery() throws Exception {
        List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);
        String object = objectMapper.writeValueAsString(BakeryUpdateRequest.builder()
                .name("newBakery").image("tempImage.jpg").address("address").latitude(35.124124).longitude(127.312452).hours("09:00~20:00")
                .instagramURL("insta").facebookURL("facebook").blogURL("blog").websiteURL("website").phoneNumber("010-1234-5678")
                .facilityInfoList(facilityInfo).status(BakeryStatus.POSTING).productList(Arrays.asList(
                        BakeryUpdateRequest.ProductUpdateRequest.builder()
                                .productId(product.getId()).productType(ProductType.BREAD)
                                .productName("testBread").price("12000").image("tempImage.jpg").build(),//,
                        BakeryUpdateRequest.ProductUpdateRequest.builder()
                                .productType(ProductType.BREAD).productName("newBread").price("10000").build()
                )).build());

        mockMvc.perform(patch("/v1/admin/bakeries/{bakeryId}", bakery.getId())
                        .content(object).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/admin/bakery/update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호")),
                        requestFields(
                                fieldWithPath("name").description("빵집 이름"),
                                fieldWithPath("image").description("빵집 이미지"),
                                fieldWithPath("address").description("빵집 도로명 주소"),
                                fieldWithPath("latitude").description("빵집 위도"),
                                fieldWithPath("longitude").description("빵집 경도"),
                                fieldWithPath("hours").optional().description("빵집 영업시간"),
                                fieldWithPath("instagramURL").optional().description("빵집 인스타그램"),
                                fieldWithPath("facebookURL").optional().description("빵집 페이스북"),
                                fieldWithPath("blogURL").optional().description("빵집 블로그"),
                                fieldWithPath("websiteURL").optional().description("빵집 홈페이지"),
                                fieldWithPath("phoneNumber").optional().description("빵집 전화번호"),
                                fieldWithPath("facilityInfoList.[]").optional()
                                        .description("빵집 시설 정보 (PARKING(\"주차가능\"),\n" +
                                                "WIFI(\"와이파이\"),\n" +
                                                "DELIVERY(\"배달\"),\n" +
                                                "PET(\"반려동물\"),\n" +
                                                "SHIPPING(\"택배\"),\n" +
                                                "BOOKING(\"예약\"))"),
                                fieldWithPath("productList").optional().description("상품 리스트"),
                                fieldWithPath("productList.[].productId").optional().description("상품 고유 번호 (새로운 빵이면 null)"),
                                fieldWithPath("productList.[].productType").description("상품 타입 (BREAD, BEVERAGE, ETC)"),
                                fieldWithPath("productList.[].productName").description("상품 이름"),
                                fieldWithPath("productList.[].price").description("상품 가격"),
                                fieldWithPath("productList.[].image").optional().optional().description("상품 이미지"),
                                fieldWithPath("status").description("빵집 게시 상태 (" +
                                        "POSTING(\"게시중\"), UNPOSTING(\"미게시\"))")
                        )
                ))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProduct() throws Exception {
        mockMvc.perform(delete("/v1/admin/bakeries/{bakeryId}/products/{productId}", bakery.getId(), product.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/admin/product/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호"),
                                parameterWithName("productId").description("상품 고유 번호"))
                ))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAdminBakeryIsNewBar() throws Exception {
        mockMvc.perform(get("/v1/admin/bakeries/{bakeryId}/is-new-bar", bakery.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/admin/is-new-bar",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호")),
                        responseFields(
                                fieldWithPath("data.adminImageIsNew").description("대표/메뉴 이미지 신규 여부"),
                                fieldWithPath("data.productAddReportIsNew").description("메뉴제보 신규 여부"),
                                fieldWithPath("data.bakeryUpdateReportIsNew").description("정보수정 신규 여부")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void getAdminImageBar() throws Exception {
        mockMvc.perform(get("/v1/admin/bakeries/{bakeryId}/image-bar", bakery.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/admin/image-bar",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호")),
                        responseFields(
                                fieldWithPath("data.bakeryReportImageNum").description("대표 이미지 갯수"),
                                fieldWithPath("data.productAddReportImageNum").description("메뉴제보 이미지 갯수"),
                                fieldWithPath("data.reviewImageNum").description("리뷰 이미지 갯수")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void getAdminImages() throws Exception {
        mockMvc.perform(get("/v1/admin/bakeries/{bakeryId}/images/{imageType}?page=0",
                        bakery.getId(), AdminBakeryImageType.BAKERY_REPORT_IMAGE.getCode())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/admin/image/all",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호"),
                                parameterWithName("imageType").description("이미지 종류 " +
                                        "(bakery-report-image (대표이미지), " +
                                        "product-add-report-image (메뉴제보이미지), " +
                                        "review-image (리뷰이미지))")),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호")),
                        responseFields(
                                fieldWithPath("data.pageNumber").description("현재 페이지 (0부터 시작)"),
                                fieldWithPath("data.numberOfElements").description("현재 페이지 데이터 수"),
                                fieldWithPath("data.size").description("페이지 크기"),
                                fieldWithPath("data.totalElements").description("전체 데이터 수"),
                                fieldWithPath("data.totalPages").description("전체 페이지 수"),
                                fieldWithPath("data.contents").description("빵집 제보 이미지 리스트"),
                                fieldWithPath("data.contents.[].imageId").description("빵집 관련 이미지 고유 번호"),
                                fieldWithPath("data.contents.[].image").description("빵집 관련 이미지"),
                                fieldWithPath("data.contents.[].isNew").description("빵집 관련 이미지 신규 여부")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void deleteAdminImage() throws Exception {
        mockMvc.perform(delete("/v1/admin/bakeries/{bakeryId}/images/{imageType}/{imageId}",
                        bakery.getId(), AdminBakeryImageType.BAKERY_REPORT_IMAGE.getCode(), bakeryReportImage.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/admin/image/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호"),
                                parameterWithName("imageType").description("이미지 종류 " +
                                        "(bakery-report-image (대표이미지), " +
                                        "product-add-report-image (메뉴제보이미지), " +
                                        "review-image (리뷰이미지))"),
                                parameterWithName("imageId").description("이미지 고유 번호"))
                ))
                .andExpect(status().isNoContent());
    }

    @Test
    void getProductAddReports() throws Exception {
        mockMvc.perform(get("/v1/admin/bakeries/{bakeryId}/product-add-reports?page=0", bakery.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/admin/productAddReport",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(parameterWithName("bakeryId").description("빵집 고유 번호")),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호")
                        ),
                        responseFields(
                                fieldWithPath("data.pageNumber").description("현재 페이지 (0부터 시작)"),
                                fieldWithPath("data.numberOfElements").description("현재 페이지 데이터 수"),
                                fieldWithPath("data.size").description("페이지 크기"),
                                fieldWithPath("data.totalElements").description("전체 데이터 수"),
                                fieldWithPath("data.totalPages").description("전체 페이지 수"),
                                fieldWithPath("data.contents").description("상품 추가 제보 리스트"),
                                fieldWithPath("data.contents.[].reportId").description("상품 추가 제보 고유 번호"),
                                fieldWithPath("data.contents.[].createdAt").description("상품 추가 제보 날짜"),
                                fieldWithPath("data.contents.[].name").description("상품 이름"),
                                fieldWithPath("data.contents.[].price").description("상품 가격"),
                                fieldWithPath("data.contents.[].nickName").description("제보한 유저 닉네임"),
                                fieldWithPath("data.contents.[].imageList").description("상품 추가 제보 이미지 리스트"),
                                fieldWithPath("data.contents.[].imageList.[].imageId").description("상품 추가 제보 이미지 고유 번호"),
                                fieldWithPath("data.contents.[].imageList.[].image").description("상품 추가 제보 이미지"),
                                fieldWithPath("data.contents.[].imageList.[].isRegistered").description("상품 추가 제보 이미지 저장 여부")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void registerProductAddImage() throws Exception {
        String object = objectMapper.writeValueAsString(ProductAddImageRegisterRequest.builder()
                .imageIdList(List.of(productAddReportImage1.getId(), productAddReportImage2.getId())).build());

        mockMvc.perform(patch("/v1/admin/bakeries/{bakeryId}/product-add-reports/{reportId}", bakery.getId(), productAddReport.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken())
                        .content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("v1/admin/productAddReport/register",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호"),
                                parameterWithName("reportId").description("빵집 제보 고유 번호")),
                        requestFields(
                                fieldWithPath("imageIdList").description("저장할 이미지 고유 번호 리스트")
                        ))
                )
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProductAddReport() throws Exception {
        mockMvc.perform(delete("/v1/admin/bakeries/{bakeryId}/product-add-reports/{reportId}", bakery.getId(), productAddReport.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/admin/productAddReport/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호"),
                                parameterWithName("reportId").description("상품 추가 제보 고유 번호"))
                ))
                .andExpect(status().isNoContent());
    }

    @Test
    void getBakeryUpdateReports() throws Exception {
        mockMvc.perform(get("/v1/admin/bakeries/{bakeryId}/update-reports?page=0", bakery.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/admin/updateReport",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(parameterWithName("bakeryId").description("빵집 고유 번호")),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호")
                        ),
                        responseFields(
                                fieldWithPath("data.pageNumber").description("현재 페이지 (0부터 시작)"),
                                fieldWithPath("data.numberOfElements").description("현재 페이지 데이터 수"),
                                fieldWithPath("data.size").description("페이지 크기"),
                                fieldWithPath("data.totalElements").description("전체 데이터 수"),
                                fieldWithPath("data.totalPages").description("전체 페이지 수"),
                                fieldWithPath("data.contents").description("빵집 수정 제보 리스트"),
                                fieldWithPath("data.contents.[].reportId").description("빵집 수정 제보 고유 번호"),
                                fieldWithPath("data.contents.[].createdAt").description("빵집 수정 제보 날짜"),
                                fieldWithPath("data.contents.[].nickName").description("제보한 유저 닉네임"),
                                fieldWithPath("data.contents.[].reason").description("빵집 수정 제보 이유"),
                                fieldWithPath("data.contents.[].content").description("빵집 수정 제보 내용"),
                                fieldWithPath("data.contents.[].imageList").description("빵집 수정 제보 이미지 리스트"),
                                fieldWithPath("data.contents.[].isChange").description("빵집 수정 제보 변경 여부")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void changeBakeryUpdateReport() throws Exception {
        mockMvc.perform(patch("/v1/admin/bakeries/{bakeryId}/update-reports/{reportId}", bakery.getId(), bakeryUpdateReport.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/admin/updateReport/change",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호"),
                                parameterWithName("reportId").description("빵집 수정 제보 고유 번호"))
                ))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBakeryUpdateReport() throws Exception {
        mockMvc.perform(delete("/v1/admin/bakeries/{bakeryId}/update-reports/{reportId}", bakery.getId(), bakeryUpdateReport.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/admin/updateReport/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호"),
                                parameterWithName("reportId").description("빵집 수정 제보 고유 번호"))
                ))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBakery() throws Exception {
        mockMvc.perform(delete("/v1/admin/bakeries/{bakeryId}", bakery.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/admin/bakery/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호"))
                ))
                .andExpect(status().isNoContent());
    }
}
