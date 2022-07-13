package com.depromeet.breadmapbackend.web.controller.search;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.Bread;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.review.BreadRating;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.security.domain.RoleType;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.utils.ControllerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.util.Collections;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SearchControllerTest extends ControllerTest {
    private User user;
    private Bakery bakery1;
    private Bakery bakery2;
    private JwtToken token;

    @BeforeEach
    public void setup() {

        user = User.builder().nickName("nickname").roleType(RoleType.USER).username("username").build();
        userRepository.save(user);
        token = jwtTokenProvider.createJwtToken(user.getUsername(), user.getRoleType().getCode());

        bakery1 = Bakery.builder().id(1L).domicileAddress("domicile").latitude(37.5596080725671).longitude(127.044235133983)
                .facilityInfoList(Collections.singletonList(FacilityInfo.PARKING)).name("bakery1").streetAddress("street").build();
        bakery2 = Bakery.builder().id(2L).domicileAddress("domicile").latitude(37.55950448505721).longitude(127.04416263787213)
                .facilityInfoList(Collections.singletonList(FacilityInfo.DELIVERY)).name("bakery2").streetAddress("street").build();
        bakeryRepository.save(bakery1);
        bakeryRepository.save(bakery2);

        Review review1 = Review.builder().user(user).bakery(bakery1).content("content1").build();
        Review review2 = Review.builder().user(user).bakery(bakery2).content("content1").build();
        reviewRepository.save(review1);
        reviewRepository.save(review2);
    }

    @AfterEach
    public void setDown() {
        reviewRepository.deleteAllInBatch();
        bakeryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    void autoComplete() throws Exception {
        mockMvc.perform(get("/search/auto?word=ba&latitude=37.560992&longitude=127.044174")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("search/auto",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        requestParameters(
                                parameterWithName("word").description("검색어"),
                                parameterWithName("latitude").description("중앙 위도"),
                                parameterWithName("longitude").description("중앙 경도")
                        ),
                        responseFields(
                                fieldWithPath("data.[].bakeryId").description("빵집 고유 번호"),
                                fieldWithPath("data.[].bakeryName").description("빵집 이름"),
                                fieldWithPath("data.[].reviewNum").description("빵집 리뷰 갯수"),
                                fieldWithPath("data.[].distance").description("빵집까지 거리")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void search() throws Exception {
        mockMvc.perform(get("/search?word=bakery1&latitude=37.560992&longitude=127.044174")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("search/search",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        requestParameters(
                                parameterWithName("word").description("검색어"),
                                parameterWithName("latitude").description("중앙 위도"),
                                parameterWithName("longitude").description("중앙 경도")
                        ),
                        responseFields(
                                fieldWithPath("data.[].bakeryId").description("빵집 고유 번호"),
                                fieldWithPath("data.[].bakeryName").description("빵집 이름"),
                                fieldWithPath("data.[].reviewNum").description("빵집 리뷰 갯수"),
                                fieldWithPath("data.[].distance").description("빵집까지 거리")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void recentKeywords() throws Exception {
        mockMvc.perform(get("/search/keywords")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("search/keywords",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        responseFields(
                                fieldWithPath("data").description("최근 검색어")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void deleteRecentKeyword() throws Exception {
        mockMvc.perform(delete("/search/keywords?keyword=bakery")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("search/keywords/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        requestParameters(
                                parameterWithName("keyword").description("삭제 키워드")
                        )
                ))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteRecentKeywordAll() throws Exception {
        mockMvc.perform(delete("/search/keywords/all")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("search/keywords/deleteAll",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token"))
                ))
                .andExpect(status().isNoContent());
    }
}