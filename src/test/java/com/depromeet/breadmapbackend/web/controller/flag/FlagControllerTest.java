package com.depromeet.breadmapbackend.web.controller.flag;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.Bread;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.depromeet.breadmapbackend.domain.review.BreadReview;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.restdocs.utils.ControllerTest;
import com.depromeet.breadmapbackend.security.domain.RoleType;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.web.controller.flag.dto.FlagRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

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
        flagBakeryRepository.deleteAllInBatch();
        flagRepository.deleteAllInBatch();
        breadReviewRepository.deleteAllInBatch();
        breadRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        user = User.builder().nickName("nickname").roleType(RoleType.USER).username("username").build();
        userRepository.save(user);
        token = jwtTokenProvider.createJwtToken(user.getUsername(), user.getRoleType().getCode());

        bakery = Bakery.builder().id(1L).domicileAddress("domicile").latitude(37.5596080725671).longitude(127.044235133983)
                .name("bakery1").streetAddress("street").image("testImage").build();
        bakery.addFacilityInfo(FacilityInfo.PARKING);
        bakeryRepository.save(bakery);

        flag = Flag.builder().user(user).name("testFlagName").color(FlagColor.ORANGE).build();
        flagRepository.save(flag);

        Bread bread = Bread.builder().bakery(bakery).name("bread1").price(3000).build();
        breadRepository.save(bread);

        BreadReview review = BreadReview.builder().user(user).bakery(bakery).bread(bread).content("content1").rating(4).build();
        breadReviewRepository.save(review);
    }

    @Test
    void findSimpleFlags() throws Exception {
        mockMvc.perform(get("/flag/simple")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("flag/findSimple",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        responseFields(
                                fieldWithPath("data.[].flagId").description("깃발 고유번호"),
                                fieldWithPath("data.[].name").description("깃발 이름"),
                                fieldWithPath("data.[].color").description("깃발 색깔")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void findFlags() throws Exception {
        FlagBakery flagBakery = FlagBakery.builder().flag(flag).bakery(bakery).build();
        flagBakeryRepository.save(flagBakery);

        mockMvc.perform(get("/flag")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("flag/find",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        responseFields(
                                fieldWithPath("data.[].flagId").description("깃발 고유번호"),
                                fieldWithPath("data.[].name").description("깃발 이름"),
                                fieldWithPath("data.[].color").description("깃발 색깔"),
                                fieldWithPath("data.[].bakeryImageList").description("깃발 빵집 사진 리스트")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
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
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        requestFields(
                                fieldWithPath("name").description("깃발 이름"),
                                fieldWithPath("color").description("깃발 색깔")
                        )
                ))
                .andExpect(status().isCreated());
    }

    @Test
    void removeFlag() throws Exception {
        mockMvc.perform(delete("/flag/{flagId}", flag.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("flag/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(parameterWithName("flagId").description("깃발 고유번호"))
                ))
                .andExpect(status().isNoContent());
    }

    @Test
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
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(parameterWithName("flagId").description("깃발 고유번호")),
                        requestFields(
                                fieldWithPath("name").description("깃발 이름"),
                                fieldWithPath("color").description("깃발 색깔")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void findBakeryByFlag() throws Exception {
        FlagBakery flagBakery = FlagBakery.builder().flag(flag).bakery(bakery).build();
        flagBakeryRepository.save(flagBakery);

        mockMvc.perform(get("/flag/{flagId}", flag.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("flag/bakeryFind",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(parameterWithName("flagId").description("깃발 고유번호")),
                        responseFields(
                                fieldWithPath("data.[].image").description("빵집 이미지"),
                                fieldWithPath("data.[].id").description("빵집 고유 번호"),
                                fieldWithPath("data.[].name").description("빵집 이름"),
                                fieldWithPath("data.[].flagNum").description("빵집 가봤어요 수"),
                                fieldWithPath("data.[].rating").description("빵집 평점"),
                                fieldWithPath("data.[].reviewNum").description("빵집 리뷰 수"),
                                fieldWithPath("data.[].simpleReviewList").description("빵집 리뷰 리스트"),
                                fieldWithPath("data.[].simpleReviewList.[].id").description("빵집 리뷰 아이디"),
                                fieldWithPath("data.[].simpleReviewList.[].content").description("빵집 리뷰 내용")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void addBakeryToFlag() throws Exception {
        mockMvc.perform(post("/flag/{flagId}", flag.getId())
                .param("bakeryId", String.valueOf(bakery.getId()))
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("flag/bakeryAdd",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(parameterWithName("flagId").description("깃발 고유번호")),
                        requestParameters(parameterWithName("bakeryId").description("깃발에 추가할 빵집 고유번호"))
                ))
                .andExpect(status().isOk());

    }

    @Test
    void removeBakeryToFlag() throws Exception {
        FlagBakery flagBakery = FlagBakery.builder().flag(flag).bakery(bakery).build();
        flagBakeryRepository.save(flagBakery);

        mockMvc.perform(delete("/flag/{flagId}/{flagBakeryId}", flag.getId(), flagBakery.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("flag/bakeryDelete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(
                                parameterWithName("flagId").description("깃발 고유번호"),
                                parameterWithName("flagBakeryId").description("깃발에 추가된 빵집 고유번호")
                        )
                ))
                .andExpect(status().isNoContent());
    }
}