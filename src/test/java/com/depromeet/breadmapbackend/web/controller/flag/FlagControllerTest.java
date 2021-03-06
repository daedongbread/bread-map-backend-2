package com.depromeet.breadmapbackend.web.controller.flag;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.Bread;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.bakery.repository.BakeryAddReportRepository;
import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.depromeet.breadmapbackend.domain.review.BreadRating;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.utils.ControllerTest;
import com.depromeet.breadmapbackend.security.domain.RoleType;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.web.controller.flag.dto.FlagRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FlagControllerTest extends ControllerTest {
    private User user;
    private JwtToken token;
    private Bakery bakery;
    private Flag flag;

    @BeforeEach
    void setUp() {
        user = User.builder().nickName("nickname").roleType(RoleType.USER).username("username").build();
        userRepository.save(user);
        token = jwtTokenProvider.createJwtToken(user.getUsername(), user.getRoleType().getCode());

        List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);
        bakery = Bakery.builder().id(1L).domicileAddress("domicile").latitude(37.5596080725671).longitude(127.044235133983)
                .facilityInfoList(facilityInfo).name("bakery1").streetAddress("street").image("testImage").build();
        bakeryRepository.save(bakery);

        flag = Flag.builder().user(user).name("testFlagName").color(FlagColor.ORANGE).build();
        flagRepository.save(flag);

        Bread bread = Bread.builder().bakery(bakery).name("bread1").price(3000).build();

        breadRepository.save(bread);

        Review review = Review.builder().user(user).bakery(bakery).content("content1").isUse(true).build();
        reviewRepository.save(review);

        BreadRating rating = BreadRating.builder().bread(bread).review(review).rating(4L).build();
        breadRatingRepository.save(rating);
    }

    @AfterEach
    public void setDown() {
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
    void findSimpleFlags() throws Exception {
        mockMvc.perform(get("/flag/simple")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("flag/findSimple",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        responseFields(
                                fieldWithPath("data.[].flagId").description("?????? ????????????"),
                                fieldWithPath("data.[].name").description("?????? ??????"),
                                fieldWithPath("data.[].color").description("?????? ??????")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
//    @Transactional
    void findFlags() throws Exception {
        FlagBakery flagBakery = FlagBakery.builder().flag(flag).bakery(bakery).build();
        flagBakeryRepository.save(flagBakery);

        mockMvc.perform(get("/flag")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("flag/find",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        responseFields(
                                fieldWithPath("data.[].flagId").description("?????? ????????????"),
                                fieldWithPath("data.[].name").description("?????? ??????"),
                                fieldWithPath("data.[].color").description("?????? ??????"),
                                fieldWithPath("data.[].bakeryImageList").description("?????? ?????? ?????? ?????????")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
//    @Transactional
    void addFlag() throws Exception {
        String object = objectMapper.writeValueAsString(
                FlagRequest.builder().name("testFlag").color(FlagColor.GREEN).build());

        ResultActions actions = mockMvc.perform(post("/flag")
                .header("Authorization", "Bearer " + token.getAccessToken())
                .content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

        actions
                .andDo(print())
                .andDo(document("flag/add",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        requestFields(
                                fieldWithPath("name").description("?????? ??????"),
                                fieldWithPath("color").description("?????? ??????")
                        )
                ))
                .andExpect(status().isCreated());
    }

    @Test
//    @Transactional
    void removeFlag() throws Exception {
        mockMvc.perform(delete("/flag/{flagId}", flag.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("flag/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        pathParameters(parameterWithName("flagId").description("?????? ????????????"))
                ))
                .andExpect(status().isNoContent());
    }

    @Test
//    @Transactional
    void updateFlag() throws Exception {
        String object = objectMapper.writeValueAsString(
                FlagRequest.builder().name("testUpdateFlag").color(FlagColor.YELLOW).build());

        ResultActions actions = mockMvc.perform(patch("/flag/{flagId}", flag.getId())
                .header("Authorization", "Bearer " + token.getAccessToken())
                .content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

        actions
                .andDo(print())
                .andDo(document("flag/update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        pathParameters(parameterWithName("flagId").description("?????? ????????????")),
                        requestFields(
                                fieldWithPath("name").description("?????? ??????"),
                                fieldWithPath("color").description("?????? ??????")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
//    @Transactional
    void findBakeryByFlag() throws Exception {
        FlagBakery flagBakery = FlagBakery.builder().flag(flag).bakery(bakery).build();
        flagBakeryRepository.save(flagBakery);

        mockMvc.perform(get("/flag/{flagId}", flag.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("flag/bakeryFind",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        pathParameters(parameterWithName("flagId").description("?????? ????????????")),
                        responseFields(
                                fieldWithPath("data.[].image").description("?????? ?????????"),
                                fieldWithPath("data.[].id").description("?????? ?????? ??????"),
                                fieldWithPath("data.[].name").description("?????? ??????"),
                                fieldWithPath("data.[].flagNum").description("?????? ???????????? ???"),
                                fieldWithPath("data.[].rating").description("?????? ??????"),
                                fieldWithPath("data.[].reviewNum").description("?????? ?????? ???"),
                                fieldWithPath("data.[].simpleReviewList").description("?????? ?????? ?????????"),
                                fieldWithPath("data.[].simpleReviewList.[].id").description("?????? ?????? ?????????"),
                                fieldWithPath("data.[].simpleReviewList.[].content").description("?????? ?????? ??????")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
//    @Transactional
    void addBakeryToFlag() throws Exception {
        mockMvc.perform(post("/flag/{flagId}", flag.getId())
                .param("bakeryId", String.valueOf(bakery.getId()))
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("flag/bakeryAdd",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        pathParameters(parameterWithName("flagId").description("?????? ????????????")),
                        requestParameters(parameterWithName("bakeryId").description("????????? ????????? ?????? ????????????"))
                ))
                .andExpect(status().isOk());

    }

    @Test
//    @Transactional
    void removeBakeryToFlag() throws Exception {
        FlagBakery flagBakery = FlagBakery.builder().flag(flag).bakery(bakery).build();
        flagBakeryRepository.save(flagBakery);

        mockMvc.perform(delete("/flag/{flagId}/{flagBakeryId}", flag.getId(), flagBakery.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("flag/bakeryDelete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("????????? Access Token")),
                        pathParameters(
                                parameterWithName("flagId").description("?????? ????????????"),
                                parameterWithName("flagBakeryId").description("????????? ????????? ?????? ????????????")
                        )
                ))
                .andExpect(status().isNoContent());
    }
}