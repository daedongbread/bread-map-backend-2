package com.depromeet.breadmapbackend.web.controller.bakery;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.Bread;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.depromeet.breadmapbackend.domain.review.BreadRating;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.utils.ControllerTest;
import com.depromeet.breadmapbackend.security.domain.RoleType;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.BakeryReportRequest;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.BakeryUpdateRequest;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.BreadReportRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
        bakery1 = Bakery.builder().id(1L).domicileAddress("domicile").latitude(37.5596080725671).longitude(127.044235133983)
                .facilityInfoList(facilityInfo).name("bakery1").streetAddress("street").build();
        bakery2 = Bakery.builder().id(2L).domicileAddress("domicile").latitude(37.55950448505721).longitude(127.04416263787213)
                .facilityInfoList(facilityInfo).name("bakery2").streetAddress("street").build();

        bakeryRepository.save(bakery1);
        bakeryRepository.save(bakery2);

        Bread bread1 = Bread.builder().bakery(bakery1).name("bread1").price(3000).build();
        Bread bread2 = Bread.builder().bakery(bakery2).name("bread2").price(4000).build();
        breadRepository.save(bread1);
        breadRepository.save(bread2);

        Review review1 = Review.builder().user(user).bakery(bakery1).content("content1").build();
        Review review2 = Review.builder().user(user).bakery(bakery2).content("content1").build();
        reviewRepository.save(review1);
        reviewRepository.save(review2);

        BreadRating rating1 = BreadRating.builder().bread(bread1).review(review1).rating(4L).build();
        BreadRating rating2 = BreadRating.builder().bread(bread2).review(review2).rating(4L).build();
        breadRatingRepository.save(rating1);
        breadRatingRepository.save(rating2);
    }

    @AfterEach
    public void setDown() {
        bakeryUpdateReportRepository.deleteAllInBatch();
        bakeryDeleteReportRepository.deleteAllInBatch();
        bakeryAddReportRepository.deleteAllInBatch();
        breadAddReportRepository.deleteAllInBatch();
        flagBakeryRepository.deleteAllInBatch();
        flagRepository.deleteAllInBatch();
        breadRatingRepository.deleteAllInBatch();
        reviewCommentLikeRepository.deleteAllInBatch();
        reviewCommentRepository.deleteAllInBatch();
        reviewLikeRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
        breadRepository.deleteAllInBatch();
        bakeryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
//    @Transactional
    void findBakeryList() throws Exception {
        mockMvc.perform(get("/bakery?sort=distance&latitude=37.560992&longitude=127.044174&latitudeDelta=0.01&longitudeDelta=0.02")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("bakery/find/default",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        requestParameters(
                                parameterWithName("sort").description("?????? ??????"),
                                parameterWithName("latitude").description("?????? ??????"),
                                parameterWithName("longitude").description("?????? ??????"),
                                parameterWithName("latitudeDelta").description("?????? ??????"),
                                parameterWithName("longitudeDelta").description("?????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("data.[].latitude").description("?????? ??????"),
                                fieldWithPath("data.[].longitude").description("?????? ??????"),
                                fieldWithPath("data.[].image").description("?????? ?????????"),
                                fieldWithPath("data.[].id").description("?????? ?????? ??????"),
                                fieldWithPath("data.[].name").description("?????? ??????"),
                                fieldWithPath("data.[].flagNum").description("?????? ???????????? ???"),
                                fieldWithPath("data.[].rating").description("?????? ??????"),
                                fieldWithPath("data.[].reviewNum").description("?????? ?????? ???"),
                                fieldWithPath("data.[].simpleReviewList").description("?????? ?????? ?????????"),
                                fieldWithPath("data.[].simpleReviewList.[].id").description("?????? ?????? ?????????"),
                                fieldWithPath("data.[].simpleReviewList.[].content").description("?????? ?????? ??????"),
                                fieldWithPath("data.[].distance").description("???????????? ??????"),
                                fieldWithPath("data.[].popularNum").description("?????? ?????????")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
//    @Transactional
    void findBakeryListByFilter() throws Exception {
        Flag flag = Flag.builder().user(user).name("testFlagName").color(FlagColor.ORANGE).build();
        flagRepository.save(flag);

        FlagBakery flagBakery = FlagBakery.builder().flag(flag).bakery(bakery1).build();
        flagBakeryRepository.save(flagBakery);

        mockMvc.perform(get("/bakery/filter?sort=distance&latitude=37.560992&longitude=127.044174&latitudeDelta=0.01&longitudeDelta=0.02")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("bakery/find/filter",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        requestParameters(
                                parameterWithName("sort").description("?????? ??????"),
                                parameterWithName("latitude").description("?????? ??????"),
                                parameterWithName("longitude").description("?????? ??????"),
                                parameterWithName("latitudeDelta").description("?????? ??????"),
                                parameterWithName("longitudeDelta").description("?????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("data.[].latitude").description("?????? ??????"),
                                fieldWithPath("data.[].longitude").description("?????? ??????"),
                                fieldWithPath("data.[].image").description("?????? ?????????"),
                                fieldWithPath("data.[].id").description("?????? ?????? ??????"),
                                fieldWithPath("data.[].name").description("?????? ??????"),
                                fieldWithPath("data.[].flagNum").description("?????? ???????????? ???"),
                                fieldWithPath("data.[].rating").description("?????? ??????"),
                                fieldWithPath("data.[].reviewNum").description("?????? ?????? ???"),
                                fieldWithPath("data.[].simpleReviewList").description("?????? ?????? ?????????"),
                                fieldWithPath("data.[].simpleReviewList.[].id").description("?????? ?????? ?????????"),
                                fieldWithPath("data.[].simpleReviewList.[].content").description("?????? ?????? ??????"),
                                fieldWithPath("data.[].distance").description("???????????? ??????"),
                                fieldWithPath("data.[].popularNum").description("?????? ?????????"),
                                fieldWithPath("data.[].color").description("?????? ?????? ??????")
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
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("?????? ?????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("data.info").description("?????? ??????"),
                                fieldWithPath("data.info.image").description("?????? ?????????"),
                                fieldWithPath("data.info.name").description("?????? ??????"),
                                fieldWithPath("data.info.flagNum").description("?????? ???????????? ???"),
                                fieldWithPath("data.info.rating").description("?????? ??????"),
                                fieldWithPath("data.info.reviewNum").description("?????? ?????? ???"),
                                fieldWithPath("data.info.domicileAddress").description("?????? ????????? ??????"),
                                fieldWithPath("data.info.streetAddress").description("?????? ?????? ??????"),
                                fieldWithPath("data.info.hours").description("?????? ?????? ??????"),
                                fieldWithPath("data.info.websiteURL").description("?????? ????????????"),
                                fieldWithPath("data.info.instagramURL").description("?????? ?????????"),
                                fieldWithPath("data.info.facebookURL").description("?????? ????????????"),
                                fieldWithPath("data.info.blogURL").description("?????? ?????????"),
                                fieldWithPath("data.info.phoneNumber").description("?????? ????????????"),
                                fieldWithPath("data.menu").description("?????? ??????"),
                                fieldWithPath("data.menu.[].id").description("??? ????????????"),
                                fieldWithPath("data.menu.[].name").description("??? ??????"),
                                fieldWithPath("data.menu.[].rating").description("??? ??????"),
                                fieldWithPath("data.menu.[].reviewNum").description("??? ?????? ???"),
                                fieldWithPath("data.menu.[].price").description("??? ??????"),
                                fieldWithPath("data.facilityInfoList").description("?????? ?????? ??????")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void bakeryUpdateReport() throws Exception {
        String object = objectMapper.writeValueAsString(BakeryUpdateRequest.builder()
                .name("newBakery").location("newLocation").content("newContent").build());

        mockMvc.perform(post("/bakery/report/{bakeryId}/update", bakery1.getId())
                .header("Authorization", "Bearer " + token.getAccessToken())
                .content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("bakery/report/update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        pathParameters(parameterWithName("bakeryId").description("?????? ?????? ??????")),
                        requestFields(
                                fieldWithPath("name").description("?????? ?????? ??????"),
                                fieldWithPath("location").description("?????? ?????? ??????"),
                                fieldWithPath("content").description("?????? ??????")
                        )
                ))
                .andExpect(status().isCreated());
    }

    @Test
    void bakeryDeleteReport() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders
                .fileUpload("/bakery/report/{bakeryId}/delete", bakery1.getId())
                .file(new MockMultipartFile("file", UUID.randomUUID().toString() +".png", "image/png", "test".getBytes()))
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("bakery/report/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        pathParameters(parameterWithName("bakeryId").description("?????? ?????? ??????")),
                        requestParts(partWithName("file").description("?????? ????????? ?????? ?????????"))
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
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        requestFields(
                                fieldWithPath("name").description("?????? ?????? ??????"),
                                fieldWithPath("location").description("?????? ?????? ??????"),
                                fieldWithPath("content").description("?????? ??????")
                        )
                ))
                .andExpect(status().isCreated());
    }

    @Test
    void breadAddReport() throws Exception {
        String object = objectMapper.writeValueAsString(BreadReportRequest.builder().name("newBread").price(4000).build());
        MockMultipartFile request =
                new MockMultipartFile("request", "", "application/json", object.getBytes());

        mockMvc.perform(RestDocumentationRequestBuilders
                .fileUpload("/bakery/report/{bakeryId}", bakery1.getId())
                .file(new MockMultipartFile("files", UUID.randomUUID().toString() +".png", "image/png", "test".getBytes()))
                .file(request).accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("bakery/report/bread",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        pathParameters(parameterWithName("bakeryId").description("?????? ?????? ??????")),
                        requestParts(
                                partWithName("request").description("?????? ??? ??????"),
                                partWithName("files").description("?????? ??? ????????????")
                        ),
                        requestPartBody("request"),
                        requestPartFields("request",
                                fieldWithPath("name").description("?????? ??? ??????"),
                                fieldWithPath("price").description("?????? ??? ??????")
                        )
                ))
                .andExpect(status().isCreated());
    }
}