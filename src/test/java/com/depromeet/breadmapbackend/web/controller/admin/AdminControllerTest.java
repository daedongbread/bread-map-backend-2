package com.depromeet.breadmapbackend.web.controller.admin;

import com.depromeet.breadmapbackend.domain.bakery.*;
import com.depromeet.breadmapbackend.domain.review.*;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.utils.ControllerTest;
import com.depromeet.breadmapbackend.security.domain.RoleType;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.web.controller.admin.dto.AddBakeryRequest;
import com.depromeet.breadmapbackend.web.controller.admin.dto.AdminLoginRequest;
import com.depromeet.breadmapbackend.web.controller.admin.dto.UpdateBakeryReportStatusRequest;
import com.depromeet.breadmapbackend.web.controller.admin.dto.UpdateBakeryRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;

import java.io.InputStream;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminControllerTest extends ControllerTest {
    private User admin;
    private User user;
    private Bakery bakery;
    private Bread bread;
    private BakeryAddReport bakeryAddReport;
    private JwtToken token;
    private ReviewReport reviewReport;

    @BeforeEach
    public void setup() {
        admin = User.builder()
                .email("adminId").nickName("adminNickName").roleType(RoleType.ADMIN)
                .username(passwordEncoder.encode("password")).build();
        userRepository.save(admin);
        token = jwtTokenProvider.createJwtToken(admin.getUsername(), admin.getRoleType().getCode());

        user = User.builder().nickName("nickname").roleType(RoleType.USER).username("username").build();
        userRepository.save(user);

        List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);
        bakery = Bakery.builder().id(1L).address("address").latitude(37.5596080725671).longitude(127.044235133983)
                .facilityInfoList(facilityInfo).name("bakery").status(BakeryStatus.POSTING).build();
        bakeryRepository.save(bakery);

        bread = Bread.builder().bakery(bakery).name("bread1").price(3000).build();
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
    void adminLogin() throws Exception {
        String object = objectMapper.writeValueAsString(AdminLoginRequest.builder()
                .adminId("adminId").password("password").build());

        ResultActions result = mockMvc.perform(post("/admin/login")
                .content(object)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andDo(print())
                .andDo(document("admin/login",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("adminId").description("관리자 아이디"),
                                fieldWithPath("password").description("관리자 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("data.accessToken").description("엑세스 토큰"),
                                fieldWithPath("data.refreshToken").description("리프레시 토큰"),
                                fieldWithPath("data.accessTokenExpiredDate").description("엑세스 토큰 만료시간")
                        )
                ))
                .andExpect(status().isCreated());
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
                                fieldWithPath("data.bakeryDtoList.[].status")
                                        .description("빵집 게시상태 (" +
                                                "POSTING(\"게시중\"),\n" +
                                                "UNPOSTING(\"미게시\"))"),
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
    void searchBakeryList() throws Exception {
        mockMvc.perform(get("/admin/bakery/search?name=bak&page=0")
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
                                fieldWithPath("data.bakeryDtoList").description("빵집 리스트"),
                                fieldWithPath("data.bakeryDtoList.[].bakeryId").description("빵집 고유 번호"),
                                fieldWithPath("data.bakeryDtoList.[].name").description("빵집 이름"),
                                fieldWithPath("data.bakeryDtoList.[].createdAt").description("빵집 최초 등록일"),
                                fieldWithPath("data.bakeryDtoList.[].modifiedAt").description("빵집 마지막 수정일"),
                                fieldWithPath("data.bakeryDtoList.[].status")
                                        .description("빵집 게시 상태 (" +
                                                "POSTING(\"게시중\"),\n" +
                                                "UNPOSTING(\"미게시\"))"),
                                fieldWithPath("data.totalNum").description("빵집 갯수")
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
        String object = objectMapper.writeValueAsString(AddBakeryRequest.builder()
                .name("newBakery").address("address").latitude(35.124124).longitude(127.312452).hours("09:00~20:00")
                .instagramURL("insta").facebookURL("facebook").blogURL("blog").websiteURL("website").phoneNumber("010-1234-5678")
                .facilityInfoList(facilityInfo).status(BakeryStatus.POSTING).breadList(Arrays.asList(
                        AddBakeryRequest.AddBreadRequest.builder().name("testBread").price(12000).build()
                )).build());
        MockMultipartFile request = new MockMultipartFile("request", "", "application/json", object.getBytes());

        mockMvc.perform(RestDocumentationRequestBuilders
                .fileUpload("/admin/bakery")
                .file(new MockMultipartFile("bakeryImage", null, "image/png", (InputStream) null))
                .file(new MockMultipartFile("breadImageList", null, "image/png", (InputStream) null))
                .file(request).accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/bakery/add",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        requestParts(
                                partWithName("request").description("빵 정보"),
                                partWithName("bakeryImage").description("빵집 이미지"),
                                partWithName("breadImageList").description("빵 이미지 " +
                                        "(request의 빵 갯수와 반드시 같아야 하며 없는 이미지는 null로 넘겨야 함)")
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
                                fieldWithPath("breadList.[].name").description("빵 이름"),
                                fieldWithPath("breadList.[].price").description("빵 가격"),
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
        String object = objectMapper.writeValueAsString(UpdateBakeryRequest.builder()
                .bakeryId(bakery.getId()).name("newBakery").address("address").latitude(35.124124).longitude(127.312452).hours("09:00~20:00")
                .instagramURL("insta").facebookURL("facebook").blogURL("blog").websiteURL("website").phoneNumber("010-1234-5678")
                .facilityInfoList(facilityInfo).status(BakeryStatus.POSTING).breadList(Arrays.asList(
                        UpdateBakeryRequest.UpdateBreadRequest.builder()
                                .breadId(bread.getId()).name("testBread").price(12000).build()
                )).build());
        MockMultipartFile request = new MockMultipartFile("request", "", "application/json", object.getBytes());

        mockMvc.perform(RestDocumentationRequestBuilders
                .fileUpload("/admin/bakery/{bakeryId}", bakery.getId())
                .file(new MockMultipartFile("bakeryImage", null, "image/png", (InputStream) null))
                .file(new MockMultipartFile("breadImageList", null, "image/png", (InputStream) null))
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
                                partWithName("request").description("빵 정보"),
                                partWithName("bakeryImage").description("빵집 이미지"),
                                partWithName("breadImageList").description("빵 이미지 " +
                                        "(request의 빵 갯수와 반드시 같아야 하며 없는 이미지는 null로 넘겨야 함)")
                        ),
                        requestPartFields("request",
                                fieldWithPath("bakeryId").description("빵집 고유 번호"),
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
                                fieldWithPath("breadList.[].breadId").description("빵 고유 번호"),
                                fieldWithPath("breadList.[].name").description("빵 이름"),
                                fieldWithPath("breadList.[].price").description("빵 가격"),
                                fieldWithPath("status")
                                        .description("빵집 게시상태 (" +
                                                "POSTING(\"게시중\"),\n" +
                                                "UNPOSTING(\"미게시\"))")
                        )
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
                                fieldWithPath("data.bakeryAddReportDtoList.[].status")
                                        .description("빵집 제보 처리 상태 " +
                                                "(BEFORE_REFLECT(\"검토전\"),\n" +
                                                "NOT_REFLECT(\"미반영\"),\n" +
                                                "REFLECT(\"반영완료\"))"),
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
        String object = objectMapper.writeValueAsString(UpdateBakeryReportStatusRequest.builder()
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
                                fieldWithPath("data.reviewReportDtoList.[].reason")
                                        .description("리뷰 신고 이유 (" +
                                                "IRRELEVANT_CONTENT(\"리뷰와 관계없는 내용\"),\n" +
                                                "INAPPROPRIATE_CONTENT(\"음란성, 욕설 등 부적절한 내용\"),\n" +
                                                "IRRELEVANT_IMAGE(\"리뷰와 관련없는 사진 게시\"),\n" +
                                                "UNFIT_CONTENT(\"리뷰 작성 취지에 맞지 않는 내용(복사글 등)\"),\n" +
                                                "COPYRIGHT_THEFT(\"저작권 도용 의심(사진 등)\"),\n" +
                                                "ETC(\"기타(하단 내용 작성)\"))"),
                                fieldWithPath("data.reviewReportDtoList.[].respondentNickName").description("피신고자 닉네임"),
                                fieldWithPath("data.reviewReportDtoList.[].reportedReviewId").description("신고된 리뷰 고유 번호"),
                                fieldWithPath("data.reviewReportDtoList.[].content").description("리뷰 신고 내용"),
                                fieldWithPath("data.reviewReportDtoList.[].createdAt").description("리뷰 신고 시간"),
                                fieldWithPath("data.reviewReportDtoList.[].status")
                                        .description("리뷰 게시 상태" +
                                                "(BLOCK,\n" +
                                                "UNBLOCK)"),
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
                                fieldWithPath("data.userDtoList.[].email").description("유저 이메일").optional(),
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
