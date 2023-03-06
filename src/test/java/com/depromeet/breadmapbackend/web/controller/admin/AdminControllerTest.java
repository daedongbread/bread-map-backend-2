package com.depromeet.breadmapbackend.web.controller.admin;

import com.depromeet.breadmapbackend.domain.admin.Admin;
import com.depromeet.breadmapbackend.domain.bakery.*;
import com.depromeet.breadmapbackend.domain.common.ImageType;
import com.depromeet.breadmapbackend.domain.product.Product;
import com.depromeet.breadmapbackend.domain.product.ProductAddReport;
import com.depromeet.breadmapbackend.domain.product.ProductAddReportImage;
import com.depromeet.breadmapbackend.domain.product.ProductType;
import com.depromeet.breadmapbackend.domain.review.*;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.utils.ControllerTest;
import com.depromeet.breadmapbackend.security.domain.RoleType;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.web.controller.admin.dto.*;
import com.depromeet.breadmapbackend.web.controller.user.dto.ReissueRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminControllerTest extends ControllerTest {
    private Admin admin;
    private User user;
    private Bakery bakery;
    private Product product;
    private Review review;
    private BakeryAddReport bakeryAddReport;
    private BakeryUpdateReport bakeryUpdateReport;
    private ProductAddReport productAddReport;
    private ProductAddReportImage productAddReportImage1;
    private ProductAddReportImage productAddReportImage2;
    private BakeryReportImage bakeryReportImage;
    private JwtToken token;
    private ReviewReport reviewReport;

    @BeforeEach
    public void setup() throws IOException {
        admin = Admin.builder().email("email").password(passwordEncoder.encode("password")).build();
        adminRepository.save(admin);
        token = jwtTokenProvider.createJwtToken(admin.getEmail(), admin.getRoleType().getCode());
        redisTemplate.opsForValue()
                .set(customRedisProperties.getKey().getAdminRefresh() + ":" + admin.getId(),
                        token.getRefreshToken(), jwtTokenProvider.getRefreshTokenExpiredDate(), TimeUnit.MILLISECONDS);

        user = User.builder().nickName("nickname").roleType(RoleType.USER).username("username").build();
        userRepository.save(user);

        List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);
        bakery = Bakery.builder().id(1L).address("address").latitude(37.5596080725671).longitude(127.044235133983)
                .facilityInfoList(facilityInfo).name("bakery").status(BakeryStatus.POSTING).build();
        bakeryRepository.save(bakery);
        bakery.updateImage(customAWSS3Properties.getCloudFront() + "/" + "bakeryImage.jpg");
        s3Uploader.upload(
                new MockMultipartFile("image", "bakeryImage.jpg", "image/jpg", "test".getBytes()),
                "bakeryImage.jpg");

        product = Product.builder().bakery(bakery).productType(ProductType.BREAD).name("bread1").price("3000").build();
        productRepository.save(product);
        product.updateImage(customAWSS3Properties.getCloudFront() + "/" + "productImage.jpg");

        bakeryAddReport = BakeryAddReport.builder().user(user).content("test content").location("test location")
                .name("test Report").build();
        bakeryAddReportRepository.save(bakeryAddReport);

        bakeryUpdateReport = BakeryUpdateReport.builder()
                .bakery(bakery).user(user).name("bakeryUpdateReport").content("name").location("test location").build();
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

        review = Review.builder().user(user).bakery(bakery).content("content1").build();
        reviewRepository.save(review);
        ReviewImage image = ReviewImage.builder().review(review).bakery(bakery).imageType(ImageType.REVIEW_IMAGE).image("reviewImage.jpg").build();
        reviewImageRepository.save(image);
        ReviewProductRating rating = ReviewProductRating.builder().bakery(bakery).product(product).review(review).rating(4L).build();
        reviewProductRatingRepository.save(rating);

        reviewReport = ReviewReport.builder()
                .reporter(user).review(review).reason(ReviewReportReason.COPYRIGHT_THEFT).content("content").build();
        reviewReportRepository.save(reviewReport);
    }

    @AfterEach
    public void setDown() {
        s3Uploader.deleteFileS3(customAWSS3Properties.getBucket() + "/bakeryImage.jpg");
        bakeryUpdateReportImageRepository.deleteAllInBatch();
        bakeryUpdateReportRepository.deleteAllInBatch();
        bakeryAddReportRepository.deleteAllInBatch();
        bakeryReportImageRepository.deleteAllInBatch();
        productAddReportImageRepository.deleteAllInBatch();
        productAddReportRepository.deleteAllInBatch();
        flagBakeryRepository.deleteAllInBatch();
        flagRepository.deleteAllInBatch();
        followRepository.deleteAllInBatch();
        reviewProductRatingRepository.deleteAllInBatch();
        reviewReportRepository.deleteAllInBatch();
        reviewCommentLikeRepository.deleteAllInBatch();
        reviewCommentRepository.deleteAllInBatch();
        reviewLikeRepository.deleteAllInBatch();
        reviewImageRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        bakeryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        adminRepository.deleteAllInBatch();
    }

    @Test
    void adminLogin() throws Exception {
        String object = objectMapper.writeValueAsString(AdminLoginRequest.builder()
                .email("email").password("password").build());

        ResultActions result = mockMvc.perform(post("/admin/login")
                .content(object)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andDo(print())
                .andDo(document("admin/login",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("관리자 이메일"),
                                fieldWithPath("password").description("관리자 비밀번호")
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
    void reissue() throws Exception {
        // given
        String object = objectMapper.writeValueAsString(ReissueRequest.builder()
                .accessToken(token.getAccessToken()).refreshToken(token.getRefreshToken()).build());

        // when
        ResultActions result = mockMvc.perform(post("/admin/reissue")
                .content(object)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result
                .andDo(print())
                .andDo(document("admin/reissue",
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
    void getAdminBar() throws Exception {
        mockMvc.perform(get("/admin/bar")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/bar",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        responseFields(
                                fieldWithPath("data.bakeryReportCount").description("`검토전`인 빵집 제보 수"),
                                fieldWithPath("data.bakeryCount").description("`미게시`인 빵집 수"),
                                fieldWithPath("data.reviewReportCount").description("숨김 처리하지 않은 리뷰 신고 수")
                        )
                ))
                .andExpect(status().isOk());
    }


    @Test
    void getBakeryList() throws Exception {
        mockMvc.perform(get("/admin/bakery?page=0")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/bakery/all",
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
                                fieldWithPath("data.contents").description("빵집 리스트"),
                                fieldWithPath("data.contents.[].bakeryId").description("빵집 고유 번호"),
                                fieldWithPath("data.contents.[].name").description("빵집 이름"),
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
        mockMvc.perform(get("/admin/bakery/{bakeryId}", bakery.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/bakery",
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
                                        .description("빵집 시설 정보 (PARKING(\"주차 가능\"),\n" +
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
    void searchBakeryList() throws Exception {
        mockMvc.perform(get("/admin/bakery/search?name=ake&page=0")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/bakery/search",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        requestParameters(
                                parameterWithName("name").description("검색어"),
                                parameterWithName("page").description("페이지 번호")
                        ),
                        responseFields(
                                fieldWithPath("data.pageNumber").description("현재 페이지 (0부터 시작)"),
                                fieldWithPath("data.numberOfElements").description("현재 페이지 데이터 수"),
                                fieldWithPath("data.size").description("페이지 크기"),
                                fieldWithPath("data.totalElements").description("전체 데이터 수"),
                                fieldWithPath("data.totalPages").description("전체 페이지 수"),
                                fieldWithPath("data.contents").description("빵집 리스트"),
                                fieldWithPath("data.contents.[].bakeryId").description("빵집 고유 번호"),
                                fieldWithPath("data.contents.[].name").description("빵집 이름"),
                                fieldWithPath("data.contents.[].createdAt").description("빵집 최초 등록일"),
                                fieldWithPath("data.contents.[].modifiedAt").description("빵집 마지막 수정일"),
                                fieldWithPath("data.contents.[].status")
                                        .description("빵집 게시 상태 (" +
                                                "POSTING(\"게시중\"),\n" +
                                                "UNPOSTING(\"미게시\"))")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void getBakeryLatitudeLongitude() throws Exception {
        mockMvc.perform(get("/admin/bakery/location?address=서울 중구 세종대로 110 서울특별시청")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/bakery/location",
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
                .name("newBakery").address("address").latitude(35.124124).longitude(127.312452).hours("09:00~20:00")
                .instagramURL("insta").facebookURL("facebook").blogURL("blog").websiteURL("website").phoneNumber("010-1234-5678")
                .facilityInfoList(facilityInfo).status(BakeryStatus.POSTING).productList(Arrays.asList(
                        BakeryAddRequest.AddProductRequest.builder()
                                .productType(ProductType.BREAD).productName("testBread").price("12000").build()
                )).build());
        MockMultipartFile request = new MockMultipartFile("request", "", "application/json", object.getBytes());

        mockMvc.perform(RestDocumentationRequestBuilders
                .fileUpload("/admin/bakery")
                .file(new MockMultipartFile("bakeryImage", UUID.randomUUID().toString() +".png", "image/png", "test".getBytes()))
                .file(new MockMultipartFile("productImageList", UUID.randomUUID().toString() +".png", "image/png", "test".getBytes()))
                .file(request).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/bakery/add",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        requestParts(
                                partWithName("request").description("빵집 정보"),
                                partWithName("bakeryImage").description("빵집 이미지"),
                                partWithName("productImageList").description("상품 이미지 " +
                                        "(request의 상품 갯수와 반드시 같아야 하며 없는 이미지는 \"\"로 넘겨야 함)")
                        ),
                        requestPartFields("request",
                                fieldWithPath("name").description("빵집 이름"),
                                fieldWithPath("address").description("빵집 도로명 주소"),
                                fieldWithPath("latitude").description("빵집 위도"),
                                fieldWithPath("longitude").description("빵집 경도"),
                                fieldWithPath("hours").description("빵집 영업시간"),
                                fieldWithPath("instagramURL").description("빵집 인스타그램"),
                                fieldWithPath("facebookURL").description("빵집 페이스북"),
                                fieldWithPath("blogURL").description("빵집 블로그"),
                                fieldWithPath("websiteURL").description("빵집 홈페이지"),
                                fieldWithPath("phoneNumber").description("빵집 전화번호"),
                                fieldWithPath("facilityInfoList.[]").description("빵집 정보"),
                                fieldWithPath("productList").description("상품 리스트"),
                                fieldWithPath("productList.[].productType").description("상품 타입 (BREAD, BEVERAGE, ETC 중 하나"),
                                fieldWithPath("productList.[].productName").description("상품 이름"),
                                fieldWithPath("productList.[].price").description("상품 가격"),
                                fieldWithPath("status")
                                        .description("빵집 게시상태 (" +
                                                "POSTING(\"게시중\"),\n" +
                                                "UNPOSTING(\"미게시\"))")
                        )
                ))
                .andExpect(status().isCreated());
    }

    @Test
    void updateBakery() throws Exception {
        List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);
        String object = objectMapper.writeValueAsString(BakeryUpdateRequest.builder()
                .name("newBakery").address("address").latitude(35.124124).longitude(127.312452).hours("09:00~20:00")
                .instagramURL("insta").facebookURL("facebook").blogURL("blog").websiteURL("website").phoneNumber("010-1234-5678")
                .facilityInfoList(facilityInfo).status(BakeryStatus.POSTING).productList(Arrays.asList(
                        BakeryUpdateRequest.UpdateProductRequest.builder()
                                .productId(product.getId()).productType(ProductType.BREAD)
                                .productName("testBread").price("12000").existedImage("image").build(),//,
                        BakeryUpdateRequest.UpdateProductRequest.builder()
                                .productType(ProductType.BREAD).productName("newBread").price("10000").build()
                )).build());
        MockMultipartFile request = new MockMultipartFile("request", "", "application/json", object.getBytes());

        mockMvc.perform(RestDocumentationRequestBuilders
                .fileUpload("/admin/bakery/{bakeryId}", bakery.getId())
                .file(new MockMultipartFile("bakeryImage", null, "image/png", (InputStream) null))
                .file(new MockMultipartFile("productImageList", null, "image/png", (InputStream) null))
                .file(new MockMultipartFile("productImageList", "newImage", "image/png", (InputStream) null))
                .file(request).accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/bakery/update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호")),
                        requestParts(
                                partWithName("request").description("빵집 정보"),
                                partWithName("bakeryImage").description("빵집 이미지"),
                                partWithName("productImageList").description("상품 이미지 " +
                                        "(request의 상품 갯수와 반드시 같아야 하며 없는 이미지는 null로 넘겨야 함)")
                        ),
                        requestPartFields("request",
                                fieldWithPath("name").description("빵집 이름"),
                                fieldWithPath("address").description("빵집 도로명 주소"),
                                fieldWithPath("latitude").description("빵집 위도"),
                                fieldWithPath("longitude").description("빵집 경도"),
                                fieldWithPath("hours").description("빵집 영업시간"),
                                fieldWithPath("instagramURL").description("빵집 인스타그램"),
                                fieldWithPath("facebookURL").description("빵집 페이스북"),
                                fieldWithPath("blogURL").description("빵집 블로그"),
                                fieldWithPath("websiteURL").description("빵집 홈페이지"),
                                fieldWithPath("phoneNumber").description("빵집 전화번호"),
                                fieldWithPath("facilityInfoList.[]").description("빵집 정보"),
                                fieldWithPath("productList.[].productId").optional().description("상품 고유 번호 (새로운 빵 추가시 제외)"),
                                fieldWithPath("productList.[].productType").description("상품 타입 (BREAD, BEVERAGE, ETC 중 하나"),
                                fieldWithPath("productList.[].productName").description("상품 이름"),
                                fieldWithPath("productList.[].price").description("상품 가격"),
                                fieldWithPath("productList.[].existedImage").optional().description("상품 기존 이미지 (없으면 null)"),
                                fieldWithPath("status")
                                        .description("빵집 게시상태 (" +
                                                "POSTING(\"게시중\"),\n" +
                                                "UNPOSTING(\"미게시\"))")
                        )
                ))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProduct() throws Exception {
        mockMvc.perform(delete("/admin/bakery/{bakeryId}/product/{productId}", bakery.getId(), product.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/product/delete",
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
    void getAdminImages() throws Exception {
        mockMvc.perform(get("/admin/bakery/{bakeryId}/image?type=bakery&page=0", bakery.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/image/all",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호")),
                        requestParameters(
                                parameterWithName("type").optional().description("이미지 종류 (bakery, product, review)"),
                                parameterWithName("page").description("페이지 번호")),
                        responseFields(
                                fieldWithPath("data.pageNumber").description("현재 페이지 (0부터 시작)"),
                                fieldWithPath("data.numberOfElements").description("현재 페이지 데이터 수"),
                                fieldWithPath("data.size").description("페이지 크기"),
                                fieldWithPath("data.totalElements").description("전체 데이터 수"),
                                fieldWithPath("data.totalPages").description("전체 페이지 수"),
                                fieldWithPath("data.contents").description("빵집 제보 이미지 리스트"),
                                fieldWithPath("data.contents.[].type").description("빵집 관련 이미지 종류 (BAKERY, PRODUCT, REVIEW)"),
                                fieldWithPath("data.contents.[].imageId").description("빵집 관련 이미지 고유 번호"),
                                fieldWithPath("data.contents.[].image").description("빵집 관련 이미지"),
                                fieldWithPath("data.contents.[].isNew").description("빵집 관련 이미지 신규 여부")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void updateBakeryImage() throws Exception {
        s3Uploader.upload(
                new MockMultipartFile("image", "productImage.jpg", "image/jpg", "test".getBytes()),
                "newBakeryImage.jpg");
        // given
        String object = objectMapper.writeValueAsString(AdminImageUpdateRequest.builder()
                .image(customAWSS3Properties.getCloudFront() + "/newBakeryImage.jpg").build());

        // when
        ResultActions result = mockMvc.perform(patch("/admin/bakery/{bakeryId}/image", bakery.getId())
                .header("Authorization", "Bearer " + token.getAccessToken())
                .content(object)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result
                .andDo(print())
                .andDo(document("admin/bakery/image/update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호")),
                        requestFields(
                                fieldWithPath("image").description("업데이트할 이미지 경로")
                        )
                ))
                .andExpect(status().isNoContent());
        s3Uploader.deleteFileS3(customAWSS3Properties.getBucket() + "/newBakeryImage.jpg");
    }

    @Test
    void updateProductImage() throws Exception {
        s3Uploader.upload(
                new MockMultipartFile("image", "productImage.jpg", "image/jpg", "test".getBytes()),
                "newProductImage.jpg");
        // given
        String object = objectMapper.writeValueAsString(AdminImageUpdateRequest.builder()
                .image(customAWSS3Properties.getCloudFront() + "/newProductImage.jpg").build());

        // when
        ResultActions result = mockMvc.perform(patch("/admin/product/{productId}/image", product.getId())
                .header("Authorization", "Bearer " + token.getAccessToken())
                .content(object)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result
                .andDo(print())
                .andDo(document("admin/product/image/update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(
                                parameterWithName("productId").description("상품 고유 번호")),
                        requestFields(
                                fieldWithPath("image").description("업데이트할 이미지 경로")
                        )
                ))
                .andExpect(status().isNoContent());
        s3Uploader.deleteFileS3(customAWSS3Properties.getBucket() + "/newProductImage.jpg");
    }

    @Test
    void downloadAdminImage() throws Exception {
        mockMvc.perform(get("/admin/image?image=bakeryImage.jpg")
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/image/download",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        requestParameters(
                                parameterWithName("image").optional().description("다운로드할 이미지 경로"))
                ))
                .andExpect(status().isOk());
    }

    @Test
    void deleteAdminImage() throws Exception {
        mockMvc.perform(delete("/admin/bakery/{bakeryId}/image/{imageId}?type=bakery", bakery.getId(), bakeryReportImage.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/image/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호"),
                                parameterWithName("imageId").description("이미지 고유 번호")),
                        requestParameters(
                                parameterWithName("type").optional().description("이미지 종류 (bakery, product, review)"))
                ))
                .andExpect(status().isNoContent());
    }

    @Test
    void getProductAddReports() throws Exception {
        mockMvc.perform(get("/admin/bakery/{bakeryId}/productAddReport?page=0", bakery.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/productAddReport",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(parameterWithName("bakeryId").description("빵집 고유 번호")),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("lastId").optional()
                                        .description("마지막으로 조회한 상품 추가 제보 고유 번호 (page가 0일때는 없어도 됨)")
                        ),
                        responseFields(
                                fieldWithPath("data.pageNumber").description("현재 페이지 (0부터 시작)"),
                                fieldWithPath("data.numberOfElements").description("현재 페이지 데이터 수"),
                                fieldWithPath("data.size").description("페이지 크기"),
                                fieldWithPath("data.totalElements").description("전체 데이터 수"),
                                fieldWithPath("data.totalPages").description("전체 페이지 수"),
                                fieldWithPath("data.contents").description("상품 추가 제보 리스트"),
                                fieldWithPath("data.contents.[].reportId").description("상품 추가 제보 고유 번호"),
                                fieldWithPath("data.contents.[].mainImage").description("상품 추가 제보 메인 이미지"),
                                fieldWithPath("data.contents.[].imageList").description("상품 추가 제보 이미지 리스트"),
                                fieldWithPath("data.contents.[].imageList.[].imageId").description("상품 추가 제보 이미지 고유 번호"),
                                fieldWithPath("data.contents.[].imageList.[].image").description("상품 추가 제보 이미지"),
                                fieldWithPath("data.contents.[].createdAt").description("상품 추가 제보 날짜"),
                                fieldWithPath("data.contents.[].name").description("상품 이름"),
                                fieldWithPath("data.contents.[].price").description("상품 가격"),
                                fieldWithPath("data.contents.[].nickName").description("제보한 유저 닉네임")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void updateProductAddImage() throws Exception {
        String object = objectMapper.writeValueAsString(ProductAddImageUpdateRequest.builder()
                        .beforeImage(productAddReportImage1.getImage()).afterId(productAddReportImage2.getId()).build());

        mockMvc.perform(patch("/admin/bakery/{bakeryId}/productAddReport/{reportId}", bakery.getId(), bakeryAddReport.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken())
                        .content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("admin/productAddReport/update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호"),
                                parameterWithName("reportId").description("빵집 제보 고유 번호")),
                        requestFields(
                                fieldWithPath("beforeImage").description("이전 메인 이미지"),
                                fieldWithPath("afterId").description("이후 메인 이미지 고유 번호")
                        ))
                )
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProductAddReport() throws Exception {
        mockMvc.perform(delete("/admin/bakery/{bakeryId}/productAddReport/{reportId}", bakery.getId(), productAddReport.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/productAddReport/delete",
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
        mockMvc.perform(get("/admin/bakery/{bakeryId}/updateReport?page=0", bakery.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/updateReport",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(parameterWithName("bakeryId").description("빵집 고유 번호")),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("lastId").optional()
                                        .description("마지막으로 조회한 상품 추가 제보 고유 번호 (page가 0일때는 없어도 됨)")
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
                                fieldWithPath("data.contents.[].content").description("빵집 수정 제보 내용"),
                                fieldWithPath("data.contents.[].imageList").description("빵집 수정 제보 이미지 리스트"),
                                fieldWithPath("data.contents.[].isChange").description("빵집 수정 제보 변경 여부")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void changeBakeryUpdateReport() throws Exception {
        mockMvc.perform(patch("/admin/bakery/{bakeryId}/updateReport/{reportId}", bakery.getId(), bakeryUpdateReport.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/updateReport/change",
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
        mockMvc.perform(delete("/admin/bakery/{bakeryId}/updateReport/{reportId}", bakery.getId(), bakeryUpdateReport.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/updateReport/delete",
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
        mockMvc.perform(delete("/admin/bakery/{bakeryId}", bakery.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/bakery/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호"))
                ))
                .andExpect(status().isNoContent());
    }

    @Test
    void getBakeryAddReportList() throws Exception {
        mockMvc.perform(get("/admin/bakery/report?page=0")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/bakeryReport/all",
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
                                fieldWithPath("data.contents").description("빵집 제보 리스트"),
                                fieldWithPath("data.contents.[].reportId").description("빵집 제보 고유 번호"),
                                fieldWithPath("data.contents.[].nickName").description("빵집 제보 유저 닉네임"),
                                fieldWithPath("data.contents.[].bakeryName").description("빵집 이름"),
                                fieldWithPath("data.contents.[].location").description("빵집 위치"),
                                fieldWithPath("data.contents.[].content").description("빵집 제보 내용"),
                                fieldWithPath("data.contents.[].createdAt").description("빵집 제보 시간"),
                                fieldWithPath("data.contents.[].status")
                                        .description("빵집 제보 처리 상태 " +
                                                "(BEFORE_REFLECT(\"검토전\"),\n" +
                                                "NOT_REFLECT(\"미반영\"),\n" +
                                                "REFLECT(\"반영완료\"))")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void getBakeryAddReport() throws Exception {
        mockMvc.perform(get("/admin/bakery/report/{reportId}", bakeryAddReport.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/bakeryReport",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(
                                parameterWithName("reportId").description("빵집 고유 번호")),
                        responseFields(
                                fieldWithPath("data.nickName").description("빵집 제보 유저 닉네임"),
                                fieldWithPath("data.bakeryName").description("빵집 이름"),
                                fieldWithPath("data.location").description("빵집 위치"),
                                fieldWithPath("data.content").description("빵집 제보 내용"),
                                fieldWithPath("data.status")
                                        .description("빵집 제보 처리 상태 " +
                                                "(BEFORE_REFLECT(\"검토전\"),\n" +
                                                "NOT_REFLECT(\"미반영\"),\n" +
                                                "REFLECT(\"반영완료\"))")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void updateBakeryAddReportStatus() throws Exception {
        String object = objectMapper.writeValueAsString(BakeryReportStatusUpdateRequest.builder()
                .status(BakeryAddReportStatus.REFLECT).build());

        mockMvc.perform(patch("/admin/bakery/report/{reportId}", bakeryAddReport.getId())
                .header("Authorization", "Bearer " + token.getAccessToken())
                .content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("admin/bakeryReport/update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(
                                parameterWithName("reportId").description("빵집 제보 고유 번호")),
                        requestFields(
                                fieldWithPath("status").description("빵집 제보 처리 상태 " +
                                        "(BEFORE_REFLECT(\"검토전\"),\n" +
                                        "NOT_REFLECT(\"미반영\"),\n" +
                                        "REFLECT(\"반영완료\"))")
                        )
                ))
                .andExpect(status().isNoContent());
    }

    @Test
    void getReviewReportList() throws Exception {
        mockMvc.perform(get("/admin/review/report?page=0")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/reviewReport/all",
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
        mockMvc.perform(patch("/admin/review/report/{reportId}", reviewReport.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/reviewReport/update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(
                                parameterWithName("reportId").description("리뷰 신고 고유 번호"))
                ))
                .andExpect(status().isNoContent());
    }

    @Test
    void getUserList() throws Exception {
        mockMvc.perform(get("/admin/user?page=0")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/user/all",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        requestParameters(
                                parameterWithName("page").description("페이지 숫자")),
                        responseFields(
                                fieldWithPath("data.pageNumber").description("현재 페이지 (0부터 시작)"),
                                fieldWithPath("data.numberOfElements").description("현재 페이지 데이터 수"),
                                fieldWithPath("data.size").description("페이지 크기"),
                                fieldWithPath("data.totalElements").description("전체 데이터 수"),
                                fieldWithPath("data.totalPages").description("전체 페이지 수"),
                                fieldWithPath("data.contents").description("유저 리스트"),
                                fieldWithPath("data.contents.[].id").description("유저 고유 번호"),
                                fieldWithPath("data.contents.[].username").description("유저 식별자"),
                                fieldWithPath("data.contents.[].nickName").description("유저 닉네임"),
                                fieldWithPath("data.contents.[].email").description("유저 이메일").optional(),
                                fieldWithPath("data.contents.[].createdAt").description("유저 가입 날짜"),
                                fieldWithPath("data.contents.[].lastAccessAt").description("유저 최종 접속"),
                                fieldWithPath("data.contents.[].roleType").description("유저 권한")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void changeUserBlock() throws Exception {
        mockMvc.perform(patch("/admin/user/{userId}/block", user.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/user/block",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(
                                parameterWithName("userId").description("차단 유저 고유 번호"))
                ))
                .andExpect(status().isNoContent());
    }
}
