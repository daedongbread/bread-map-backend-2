package com.depromeet.breadmapbackend.web.controller.flag;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.product.Product;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
import com.depromeet.breadmapbackend.domain.flag.FlagColor;
import com.depromeet.breadmapbackend.domain.product.ProductType;
import com.depromeet.breadmapbackend.domain.review.ReviewProductRating;
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
        bakery = Bakery.builder().id(1L).address("address").latitude(37.5596080725671).longitude(127.044235133983)
                .facilityInfoList(facilityInfo).name("bakery1").status(BakeryStatus.POSTING).build();
        bakery.updateImage("testImage");
        bakeryRepository.save(bakery);

        flag = Flag.builder().user(user).name("testFlagName").color(FlagColor.ORANGE).build();
        flagRepository.save(flag);

        Product product = Product.builder().bakery(bakery).productType(ProductType.BREAD).name("bread1").price("3000").build();

        productRepository.save(product);

        Review review = Review.builder().user(user).bakery(bakery).content("content1").build();
        reviewRepository.save(review);

        ReviewProductRating rating = ReviewProductRating.builder().bakery(bakery).product(product).review(review).rating(4L).build();
        reviewProductRatingRepository.save(rating);
    }

    @AfterEach
    public void setDown() {
        flagBakeryRepository.deleteAllInBatch();
        flagRepository.deleteAllInBatch();
        reviewProductRatingRepository.deleteAllInBatch();
        reviewCommentLikeRepository.deleteAllInBatch();
        reviewCommentRepository.deleteAllInBatch();
        reviewLikeRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        bakeryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
//    @Transactional
    void findFlags() throws Exception {
        FlagBakery flagBakery = FlagBakery.builder().flag(flag).bakery(bakery).user(user).build();
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
                                fieldWithPath("data.[].color")
                                        .description("깃발 색깔 (ORANGE(\"주황색\"),\n" +
                                                "GREEN(\"초록색\"),\n" +
                                                "YELLOW(\"노란색\"),\n" +
                                                "CYAN(\"청록색\"),\n" +
                                                "BLUE(\"초록색\"),\n" +
                                                "SKY(\"하늘색\"),\n" +
                                                "NAVY(\"네이비색\"),\n" +
                                                "PURPLE(\"보라색\"),\n" +
                                                "RED(\"빨간색\"),\n" +
                                                "PINK(\"핑크색\"))"),
                                fieldWithPath("data.[].bakeryImageList").description("깃발 빵집 사진 리스트 (최대 3개)")
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
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        requestFields(
                                fieldWithPath("name").description("깃발 이름"),
                                fieldWithPath("color")
                                        .description("깃발 색깔 (ORANGE(\"주황색\"),\n" +
                                                "GREEN(\"초록색\"),\n" +
                                                "YELLOW(\"노란색\"),\n" +
                                                "CYAN(\"청록색\"),\n" +
                                                "BLUE(\"초록색\"),\n" +
                                                "SKY(\"하늘색\"),\n" +
                                                "NAVY(\"네이비색\"),\n" +
                                                "PURPLE(\"보라색\"),\n" +
                                                "RED(\"빨간색\"),\n" +
                                                "PINK(\"핑크색\"))")
                        )
                ))
                .andExpect(status().isCreated());
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
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(parameterWithName("flagId").description("깃발 고유번호")),
                        requestFields(
                                fieldWithPath("name").description("깃발 이름"),
                                fieldWithPath("color")
                                        .description("깃발 색깔 (ORANGE(\"주황색\"),\n" +
                                                "GREEN(\"초록색\"),\n" +
                                                "YELLOW(\"노란색\"),\n" +
                                                "CYAN(\"청록색\"),\n" +
                                                "BLUE(\"초록색\"),\n" +
                                                "SKY(\"하늘색\"),\n" +
                                                "NAVY(\"네이비색\"),\n" +
                                                "PURPLE(\"보라색\"),\n" +
                                                "RED(\"빨간색\"),\n" +
                                                "PINK(\"핑크색\"))")
                        )
                ))
                .andExpect(status().isNoContent());
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
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(parameterWithName("flagId").description("깃발 고유번호"))
                ))
                .andExpect(status().isNoContent());
    }

    @Test
//    @Transactional
    void findBakeryByFlag() throws Exception {
        FlagBakery flagBakery = FlagBakery.builder().flag(flag).bakery(bakery).user(user).build();
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
//    @Transactional
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
                .andExpect(status().isCreated());

    }

    @Test
//    @Transactional
    void removeBakeryToFlag() throws Exception {
        FlagBakery flagBakery = FlagBakery.builder().flag(flag).bakery(bakery).user(user).build();
        flagBakeryRepository.save(flagBakery);

        mockMvc.perform(delete("/flag/{flagId}/bakery/{bakeryId}", flag.getId(), bakery.getId())
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("flag/bakeryDelete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(
                                parameterWithName("flagId").description("깃발 고유번호"),
                                parameterWithName("bakeryId").description("깃발에 추가된 빵집 고유번호")
                        )
                ))
                .andExpect(status().isNoContent());
    }
}