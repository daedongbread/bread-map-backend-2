package com.depromeet.breadmapbackend.web.controller.admin;

import com.depromeet.breadmapbackend.domain.bakery.*;
import com.depromeet.breadmapbackend.domain.review.*;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.utils.ControllerTest;
import com.depromeet.breadmapbackend.security.domain.RoleType;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.web.controller.admin.dto.AddBakeryRequest;
import com.depromeet.breadmapbackend.web.controller.admin.dto.UpdateBakeryReportStatusRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

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
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminControllerTest extends ControllerTest {
    private User admin;
    private User user;
    private Bakery bakery;
    private BakeryAddReport bakeryAddReport;
    private JwtToken token;
    private ReviewReport reviewReport;

    @BeforeEach
    public void setup() {
        admin = User.builder().nickName("adminNickName").roleType(RoleType.ADMIN).username("adminUserName").build();
        userRepository.save(admin);
        token = jwtTokenProvider.createJwtToken(admin.getUsername(), admin.getRoleType().getCode());

        user = User.builder().nickName("nickname").roleType(RoleType.USER).username("username").build();
        userRepository.save(user);

        List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);
        bakery = Bakery.builder().id(1L).address("address").latitude(37.5596080725671).longitude(127.044235133983)
                .facilityInfoList(facilityInfo).name("bakery").status(BakeryStatus.posting).build();
        bakeryRepository.save(bakery);

        Bread bread = Bread.builder().bakery(bakery).name("bread1").price(3000).build();
        breadRepository.save(bread);

        bakeryAddReport = BakeryAddReport.builder().user(user).content("test content").location("test location")
                .name("test Report").build();
        bakeryAddReportRepository.save(bakeryAddReport);

        Review review = Review.builder().user(user).bakery(bakery).content("content1").build();
        reviewRepository.save(review);

        BreadRating rating = BreadRating.builder().bread(bread).review(review).rating(4L).build();
        breadRatingRepository.save(rating);

        reviewReport = ReviewReport.builder()
                .reporter(user).review(review).reason(ReviewReportReason.COPYRIGHT_THEFT).content("content").build();
        reviewReportRepository.save(reviewReport);
    }

    @AfterEach
    public void setDown() {
        bakeryUpdateReportRepository.deleteAllInBatch();
        bakeryDeleteReportRepository.deleteAllInBatch();
        bakeryAddReportRepository.deleteAllInBatch();
        breadAddReportRepository.deleteAllInBatch();
        flagBakeryRepository.deleteAllInBatch();
        flagRepository.deleteAllInBatch();
        followRepository.deleteAllInBatch();
        breadRatingRepository.deleteAllInBatch();
        reviewReportRepository.deleteAllInBatch();
        reviewCommentLikeRepository.deleteAllInBatch();
        reviewCommentRepository.deleteAllInBatch();
        reviewLikeRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
        breadRepository.deleteAllInBatch();
        bakeryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    void getAllBakeryList() throws Exception {
        mockMvc.perform(get("/admin/bakery/all?page=0")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/bakery/all",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호")),
                        responseFields(
                                fieldWithPath("data.bakeryDtoList").description("빵집 리스트"),
                                fieldWithPath("data.bakeryDtoList.[].bakeryId").description("빵집 고유 번호"),
                                fieldWithPath("data.bakeryDtoList.[].name").description("빵집 이름"),
                                fieldWithPath("data.bakeryDtoList.[].createdAt").description("빵집 최초 등록일"),
                                fieldWithPath("data.bakeryDtoList.[].modifiedAt").description("빵집 마지막 수정일"),
                                fieldWithPath("data.bakeryDtoList.[].status").description("빵집 게시상태"),
                                fieldWithPath("data.totalNum").description("빵집 갯수")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void getBakeryDetail() throws Exception {
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
                                fieldWithPath("data.hours").description("빵집 영업 시간"),
                                fieldWithPath("data.websiteURL").description("빵집 홈페이지"),
                                fieldWithPath("data.instagramURL").description("빵집 인스타그램"),
                                fieldWithPath("data.facebookURL").description("빵집 페이스북"),
                                fieldWithPath("data.blogURL").description("빵집 블로그"),
                                fieldWithPath("data.phoneNumber").description("빵집 전화번호"),
                                fieldWithPath("data.menu").description("빵집 메뉴"),
                                fieldWithPath("data.menu.[].name").description("빵 이름"),
                                fieldWithPath("data.menu.[].price").description("빵 가격"),
                                fieldWithPath("data.menu.[].image").description("빵 이미지")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void getBakeryLatitudeLongitude() throws Exception {
//        mockMvc.perform(get("/admin/bakery/location?address=")
//                .header("Authorization", "Bearer " + token.getAccessToken()))
//                .andDo(print())
//                .andDo(document("admin/bakery/location",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint()),
//                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
//                        requestParameters(
//                                parameterWithName("address").description("조회 주소")),
//                        responseFields(
//                                fieldWithPath("data.latitude").description("빵집 위도"),
//                                fieldWithPath("data.longitude").description("빵집 경도")
//                        )
//                ))
//                .andExpect(status().isOk());
    }

    @Test
    void addUpdateBakery() throws Exception {
//        List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);
//
//        String object = objectMapper.writeValueAsString(AddBakeryRequest.builder()
//                .bakeryId(1L).name("newBakery").address("address")
//                .hours("09:00~20:00").instagramURL("").facebookURL("").blogURL("")
//                .websiteURL("https://test.test.com").phoneNumber("01012345678")
//                .facilityInfoList(facilityInfo).breadList(Arrays.asList(
//                        AddBakeryRequest.AddBreadRequest.builder().name("testBread").price(12000).build(),
//                        AddBakeryRequest.AddBreadRequest.builder().name("testBread2").price(22000).build()
//                )).build());
//
//        mockMvc.perform(post("/admin/bakery")
//                .header("Authorization", "Bearer " + token.getAccessToken())
//                .content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andDo(document("admin/bakery/add",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint()),
//                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
//                        requestFields(
//                                fieldWithPath("id").description("빵집 고유 번호"),
//                                fieldWithPath("name").description("빵집 이름"),
//                                fieldWithPath("imageList").description("빵집 이미지"),
//                                fieldWithPath("streetAddress").description("빵집 주소"),
//                                fieldWithPath("domicileAddress").description("빵집 도로명주소"),
//                                fieldWithPath("hours").description("빵집 영업시간"),
//                                fieldWithPath("instagramURL").description("빵집 인스타그램"),
//                                fieldWithPath("facebookURL").description("빵집 페이스북"),
//                                fieldWithPath("blogURL").description("빵집 블로그"),
//                                fieldWithPath("websiteURL").description("빵집 홈페이지"),
//                                fieldWithPath("phoneNumber").description("빵집 전화번호"),
//                                fieldWithPath("facilityInfoList.[]").description("빵집 정보"),
//                                fieldWithPath("breadList.[].name").description("빵 이름"),
//                                fieldWithPath("breadList.[].price").description("빵 가격")
//                        )
//                ))
//                .andExpect(status().isCreated());
    }

    @Test
    void updateBakery() throws Exception {

    }

    @Test
    void getBakeryReportList() throws Exception {
        mockMvc.perform(get("/admin/bakery/report/all?page=0")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/bakeryReport/all",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호")),
                        responseFields(
                                fieldWithPath("data.bakeryAddReportDtoList").description("빵집 제보 리스트"),
                                fieldWithPath("data.bakeryAddReportDtoList.[].reportId").description("빵집 제보 고유 번호"),
                                fieldWithPath("data.bakeryAddReportDtoList.[].nickName").description("빵집 제보 유저 닉네임"),
                                fieldWithPath("data.bakeryAddReportDtoList.[].bakeryName").description("빵집 이름"),
                                fieldWithPath("data.bakeryAddReportDtoList.[].location").description("빵집 위치"),
                                fieldWithPath("data.bakeryAddReportDtoList.[].content").description("빵집 제보 내용"),
                                fieldWithPath("data.bakeryAddReportDtoList.[].createdAt").description("빵집 제보 시간"),
                                fieldWithPath("data.bakeryAddReportDtoList.[].status").description("빵집 제보 처리 상태"),
                                fieldWithPath("data.totalNum").description("빵집 제보 갯수")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void getBakeryReport() throws Exception {
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
                                fieldWithPath("data.status").description("빵집 제보 처리 상태")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void updateBakeryAddReportStatus() throws Exception {
        String object = objectMapper.writeValueAsString(UpdateBakeryReportStatusRequest.builder()
                .status(BakeryAddReportStatus.reflect).build());

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
                                fieldWithPath("status").description("빵집 제보 처리 상태"))
                ))
                .andExpect(status().isNoContent());
    }

    @Test
    void getReviewReportList() throws Exception {
        mockMvc.perform(get("/admin/review/report/all?page=0")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/reviewReport/all",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호")),
                        responseFields(
                                fieldWithPath("data.reviewReportDtoList").description("리뷰 신고 리스트"),
                                fieldWithPath("data.reviewReportDtoList.[].reviewReportId").description("리뷰 신고 고유 번호"),
                                fieldWithPath("data.reviewReportDtoList.[].reporterNickName").description("신고자 닉네임"),
                                fieldWithPath("data.reviewReportDtoList.[].reason").description("리뷰 신고 이유"),
                                fieldWithPath("data.reviewReportDtoList.[].respondentNickName").description("피신고자 닉네임"),
                                fieldWithPath("data.reviewReportDtoList.[].reportedReviewId").description("신고된 리뷰 고유 번호"),
                                fieldWithPath("data.reviewReportDtoList.[].content").description("리뷰 신고 내용"),
                                fieldWithPath("data.reviewReportDtoList.[].createdAt").description("리뷰 신고 시간"),
                                fieldWithPath("data.reviewReportDtoList.[].status").description("리뷰 신고 처리 상태"),
                                fieldWithPath("data.totalNum").description("리뷰 신고 갯수")
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
        mockMvc.perform(get("/admin/user/all?page=0")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/user/all",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        requestParameters(
                                parameterWithName("page").description("페이지 숫자")),
                        responseFields(
                                fieldWithPath("data.userDtoList").description("유저 리스트"),
                                fieldWithPath("data.userDtoList.[].id").description("유저 고유 번호"),
                                fieldWithPath("data.userDtoList.[].username").description("유저 식별자"),
                                fieldWithPath("data.userDtoList.[].nickName").description("유저 닉네임"),
                                fieldWithPath("data.userDtoList.[].email").description("유저 이메일"),
                                fieldWithPath("data.userDtoList.[].createdAt").description("유저 가입 날짜"),
                                fieldWithPath("data.userDtoList.[].lastAccessAt").description("유저 최종 접속"),
                                fieldWithPath("data.userDtoList.[].roleType").description("유저 권한"),
                                fieldWithPath("data.totalNum").description("유저 갯수")
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
