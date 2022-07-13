package com.depromeet.breadmapbackend.web.controller.admin;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryAddReport;
import com.depromeet.breadmapbackend.domain.bakery.Bread;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.bakery.repository.BakeryAddReportRepository;
import com.depromeet.breadmapbackend.domain.review.*;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.utils.ControllerTest;
import com.depromeet.breadmapbackend.security.domain.RoleType;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.web.controller.admin.dto.AddBakeryRequest;
import com.depromeet.breadmapbackend.web.controller.admin.dto.UpdateBakeryReportStatusRequest;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.BakeryReportDto;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.BakeryUpdateRequest;
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
    private User user;
    private Bakery bakery1;
    private BakeryAddReport report1;
    private JwtToken token;

    @BeforeEach
    public void setup() {
        user = User.builder().nickName("nickname").roleType(RoleType.ADMIN).username("username").build();
        userRepository.save(user);
        token = jwtTokenProvider.createJwtToken(user.getUsername(), user.getRoleType().getCode());

        List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);
        bakery1 = Bakery.builder().id(1L).domicileAddress("domicile").latitude(37.5596080725671).longitude(127.044235133983)
                .facilityInfoList(facilityInfo).name("bakery1").streetAddress("street").build();
        bakeryRepository.save(bakery1);

        Bread bread1 = Bread.builder().bakery(bakery1).name("bread1").price(3000).build();
        breadRepository.save(bread1);

        report1 = BakeryAddReport.builder().user(user).content("test content").location("test location")
                .name("test Report").build();
        bakeryAddReportRepository.save(report1);

        Review review1 = Review.builder().user(user).bakery(bakery1).content("content1").build();
        reviewRepository.save(review1);

        BreadRating rating1 = BreadRating.builder().bread(bread1).review(review1).rating(4L).build();
        breadRatingRepository.save(rating1);
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
        mockMvc.perform(get("/admin/getAllBakery")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/getAllBakery",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        responseFields(
                                fieldWithPath("data.[].id").type("Number").description("빵집 id"),
                                fieldWithPath("data.[].use").type("Boolean").description("빵집 게시현황"),
                                fieldWithPath("data.[].name").type("String").description("빵집 이름"),
                                fieldWithPath("data.[].createdAt").type("String").description("빵집 생성 일자"),
                                fieldWithPath("data.[].modifiedAt").type("String").description("빵집 수정일자")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void getBakeryDetail() throws Exception {
        mockMvc.perform(get("/admin/getBakery/{bakeryId}", bakery1.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/getBakery",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호")
                        ),
                        responseFields(
                                fieldWithPath("data.name").type("String").description("빵집 이름"),
                                fieldWithPath("data.image").type("String").description("빵집 이미지"),
                                fieldWithPath("data.domicileAddress").type("String").description("빵집 주소"),
                                fieldWithPath("data.streetAddress").type("String").description("빵집 도로명주소"),
                                fieldWithPath("data.hours").type("String").description("빵집 영업시간"),
                                fieldWithPath("data.websiteURL").type("String").description("빵집 홈페이지"),
                                fieldWithPath("data.instagramURL").type("String").description("빵집 인스타그램"),
                                fieldWithPath("data.facebookURL").type("String").description("빵집 페이스북"),
                                fieldWithPath("data.blogURL").type("String").description("빵집 블로그"),
                                fieldWithPath("data.phoneNumber").type("String").description("빵집 전화번호")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void addBakery() throws Exception {
        List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);

        String object = objectMapper.writeValueAsString(AddBakeryRequest.builder()
                .id(1L).name("newBakery").imageList("").streetAddress("newLocation").domicileAddress("newLocation")
                .hours("09:00~20:00").instagramURL("").facebookURL("").blogURL("")
                .websiteURL("https://test.test.com").phoneNumber("01012345678")
                .facilityInfoList(facilityInfo).breadList(Arrays.asList(
                        AddBakeryRequest.AddBreadRequest.builder().name("testBread").price(12000).build(),
                        AddBakeryRequest.AddBreadRequest.builder().name("testBread2").price(22000).build()
                )).build());

        mockMvc.perform(post("/admin/addBakery")
                        .header("Authorization", "Bearer " + token.getAccessToken())
                        .content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("admin/addBakery",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        requestFields(
                                fieldWithPath("id").type("String").description("빵집 고유 번호"),
                                fieldWithPath("name").type("String").description("빵집 이름"),
                                fieldWithPath("imageList").type("String").description("빵집 이미지"),
                                fieldWithPath("streetAddress").type("String").description("빵집 주소"),
                                fieldWithPath("domicileAddress").type("String").description("빵집 도로명주소"),
                                fieldWithPath("hours").type("String").description("빵집 영업시간"),
                                fieldWithPath("instagramURL").type("String").description("빵집 인스타그램"),
                                fieldWithPath("facebookURL").type("String").description("빵집 페이스북"),
                                fieldWithPath("blogURL").type("String").description("빵집 블로그"),
                                fieldWithPath("websiteURL").type("String").description("빵집 홈페이지"),
                                fieldWithPath("phoneNumber").type("String").description("빵집 전화번호"),
                                fieldWithPath("facilityInfoList.[]").type("Array").description("빵집 정보"),
                                fieldWithPath("breadList.[].name").type("String").description("빵 이름"),
                                fieldWithPath("breadList.[].imageList").type("String").description("빵 이미지"),
                                fieldWithPath("breadList.[].price").type("Number").description("빵 가격")
                        )
                ))
                .andExpect(status().isCreated());
    }

    @Test
    void getAllBakeryReport() throws Exception {
        mockMvc.perform(get("/admin/getAllBakeryReport")
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/getAllBakeryReport",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        responseFields(
                                fieldWithPath("data.[].id").type("Number").description("빵집 제보 id"),
                                fieldWithPath("data.[].userId").type("Number").description("빵집 제보자 id"),
                                fieldWithPath("data.[].name").type("String").description("빵집 이름"),
                                fieldWithPath("data.[].createdAt").type("String").description("빵집 제보 시간"),
                                fieldWithPath("data.[].location").type("String").description("빵집 위치"),
                                fieldWithPath("data.[].content").type("String").description("빵집 제보 내용"),
                                fieldWithPath("data.[].status").type("Number").description("빵집 제보 처리 상태 (1: 검토전, 2: 검토중, 3: 추가완료)")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void getBakeryReportDetail() throws Exception {
        mockMvc.perform(get("/admin/getBakeryReport/{reportId}", report1.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("admin/getBakeryReport",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(
                                parameterWithName("reportId").description("빵집 고유 번호")
                        ),
                        responseFields(
                                fieldWithPath("data.id").type("Number").description("빵집 제보 id"),
                                fieldWithPath("data.userId").type("Number").description("빵집 제보자 id"),
                                fieldWithPath("data.name").type("String").description("빵집 이름"),
                                fieldWithPath("data.createdAt").type("String").description("빵집 제보 시간"),
                                fieldWithPath("data.location").type("String").description("빵집 위치"),
                                fieldWithPath("data.content").type("String").description("빵집 제보 내용"),
                                fieldWithPath("data.status").type("Number").description("빵집 제보 처리 상태 (0: 검토전, 1: 검토중, 2: 추가완료)")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void updateBakeryReport() throws Exception {
        String object = objectMapper.writeValueAsString(UpdateBakeryReportStatusRequest.builder()
                .status(1).build());

        mockMvc.perform(post("/admin/updateBakeryReport/{reportId}", report1.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken())
                        .content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("admin/updateBakeryReport",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(
                                parameterWithName("reportId").description("빵집 제보 id")
                        ),
                        requestFields(
                                fieldWithPath("status").type("Number").description("빵집 제보 처리 상태")
                        )
                ))
                .andExpect(status().isCreated());
    }
}
