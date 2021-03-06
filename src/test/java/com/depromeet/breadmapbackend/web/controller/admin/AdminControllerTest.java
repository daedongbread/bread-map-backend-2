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
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        responseFields(
                                fieldWithPath("data.[].id").type("Number").description("?????? id"),
                                fieldWithPath("data.[].use").type("Boolean").description("?????? ????????????"),
                                fieldWithPath("data.[].name").type("String").description("?????? ??????"),
                                fieldWithPath("data.[].createdAt").type("String").description("?????? ?????? ??????"),
                                fieldWithPath("data.[].modifiedAt").type("String").description("?????? ????????????")
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
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("?????? ?????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("data.name").type("String").description("?????? ??????"),
                                fieldWithPath("data.image").type("String").description("?????? ?????????"),
                                fieldWithPath("data.domicileAddress").type("String").description("?????? ??????"),
                                fieldWithPath("data.streetAddress").type("String").description("?????? ???????????????"),
                                fieldWithPath("data.hours").type("String").description("?????? ????????????"),
                                fieldWithPath("data.websiteURL").type("String").description("?????? ????????????"),
                                fieldWithPath("data.instagramURL").type("String").description("?????? ???????????????"),
                                fieldWithPath("data.facebookURL").type("String").description("?????? ????????????"),
                                fieldWithPath("data.blogURL").type("String").description("?????? ?????????"),
                                fieldWithPath("data.phoneNumber").type("String").description("?????? ????????????")
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
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        requestFields(
                                fieldWithPath("id").type("String").description("?????? ?????? ??????"),
                                fieldWithPath("name").type("String").description("?????? ??????"),
                                fieldWithPath("imageList").type("String").description("?????? ?????????"),
                                fieldWithPath("streetAddress").type("String").description("?????? ??????"),
                                fieldWithPath("domicileAddress").type("String").description("?????? ???????????????"),
                                fieldWithPath("hours").type("String").description("?????? ????????????"),
                                fieldWithPath("instagramURL").type("String").description("?????? ???????????????"),
                                fieldWithPath("facebookURL").type("String").description("?????? ????????????"),
                                fieldWithPath("blogURL").type("String").description("?????? ?????????"),
                                fieldWithPath("websiteURL").type("String").description("?????? ????????????"),
                                fieldWithPath("phoneNumber").type("String").description("?????? ????????????"),
                                fieldWithPath("facilityInfoList.[]").type("Array").description("?????? ??????"),
                                fieldWithPath("breadList.[].name").type("String").description("??? ??????"),
                                fieldWithPath("breadList.[].imageList").type("String").description("??? ?????????"),
                                fieldWithPath("breadList.[].price").type("Number").description("??? ??????")
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
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        responseFields(
                                fieldWithPath("data.[].id").type("Number").description("?????? ?????? id"),
                                fieldWithPath("data.[].userId").type("Number").description("?????? ????????? id"),
                                fieldWithPath("data.[].name").type("String").description("?????? ??????"),
                                fieldWithPath("data.[].createdAt").type("String").description("?????? ?????? ??????"),
                                fieldWithPath("data.[].location").type("String").description("?????? ??????"),
                                fieldWithPath("data.[].content").type("String").description("?????? ?????? ??????"),
                                fieldWithPath("data.[].status").type("Number").description("?????? ?????? ?????? ?????? (1: ?????????, 2: ?????????, 3: ????????????)")
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
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        pathParameters(
                                parameterWithName("reportId").description("?????? ?????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("data.id").type("Number").description("?????? ?????? id"),
                                fieldWithPath("data.userId").type("Number").description("?????? ????????? id"),
                                fieldWithPath("data.name").type("String").description("?????? ??????"),
                                fieldWithPath("data.createdAt").type("String").description("?????? ?????? ??????"),
                                fieldWithPath("data.location").type("String").description("?????? ??????"),
                                fieldWithPath("data.content").type("String").description("?????? ?????? ??????"),
                                fieldWithPath("data.status").type("Number").description("?????? ?????? ?????? ?????? (0: ?????????, 1: ?????????, 2: ????????????)")
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
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        pathParameters(
                                parameterWithName("reportId").description("?????? ?????? id")
                        ),
                        requestFields(
                                fieldWithPath("status").type("Number").description("?????? ?????? ?????? ??????")
                        )
                ))
                .andExpect(status().isCreated());
    }
}
