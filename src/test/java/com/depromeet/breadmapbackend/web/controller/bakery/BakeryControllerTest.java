package com.depromeet.breadmapbackend.web.controller.bakery;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.Bread;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.review.BreadReview;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.restdocs.utils.ControllerTest;
import com.depromeet.breadmapbackend.security.domain.RoleType;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BakeryControllerTest extends ControllerTest {
    private User user;
    private Bakery bakery1;
    private Bakery bakery2;
    private JwtToken token;

    @BeforeEach
    public void setup() {
        breadReviewRepository.deleteAllInBatch();
        breadRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        user = User.builder().nickName("nickname").roleType(RoleType.USER).username("username").build();
        userRepository.save(user);
        token = jwtTokenProvider.createJwtToken(user.getUsername(), user.getRoleType().getCode());

        bakery1 = Bakery.builder().id(1L).domicileAddress("domicile").latitude(37.5596080725671).longitude(127.044235133983)
                .name("bakery1").streetAddress("street").facilityInfoList(new ArrayList<FacilityInfo>(Arrays.asList(FacilityInfo.PARKING))).build();
        bakery2 = Bakery.builder().id(2L).domicileAddress("domicile").latitude(37.55950448505721).longitude(127.04416263787213)
                .name("bakery2").streetAddress("street").facilityInfoList(new ArrayList<FacilityInfo>(Arrays.asList(FacilityInfo.DELIVERY))).build();
        bakeryRepository.save(bakery1);
        bakeryRepository.save(bakery2);

        Bread bread1 = Bread.builder().bakery(bakery1).name("bread1").price(3000).build();
        Bread bread2 = Bread.builder().bakery(bakery2).name("bread2").price(4000).build();
        breadRepository.save(bread1);
        breadRepository.save(bread2);

        BreadReview review1 = BreadReview.builder().user(user).bakery(bakery1).bread(bread1).content("content1").rating(4).build();
        BreadReview review2 = BreadReview.builder().user(user).bakery(bakery2).bread(bread2).content("content1").rating(4).build();
        breadReviewRepository.save(review1);
        breadReviewRepository.save(review2);
    }

    @Test
//    @Transactional
    void findBakeryList() throws Exception {
        mockMvc.perform(get("/bakery/default?sort=distance&latitude=37.560992&longitude=127.044174&latitudeDelta=0.01&longitudeDelta=0.02")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("bakery/find/default",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("sort").description("정렬 방법"),
                                parameterWithName("latitude").description("중앙 위도"),
                                parameterWithName("longitude").description("중앙 경도"),
                                parameterWithName("latitudeDelta").description("위도 범위"),
                                parameterWithName("longitudeDelta").description("경도 범위")
                        ),
                        responseFields(
                                fieldWithPath("data.[].latitude").description("빵집 위도"),
                                fieldWithPath("data.[].longitude").description("빵집 경도"),
                                fieldWithPath("data.[].image").description("빵집 이미지"),
                                fieldWithPath("data.[].id").description("빵집 고유 번호"),
                                fieldWithPath("data.[].name").description("빵집 이름"),
                                fieldWithPath("data.[].flagNum").description("빵집 가봤어요 수"),
                                fieldWithPath("data.[].rating").description("빵집 평점"),
                                fieldWithPath("data.[].reviewNum").description("빵집 리뷰 수"),
                                fieldWithPath("data.[].simpleReviewList").description("빵집 리뷰 리스트"),
                                fieldWithPath("data.[].simpleReviewList.[].id").description("빵집 리뷰 아이디"),
                                fieldWithPath("data.[].simpleReviewList.[].content").description("빵집 리뷰 내용"),
                                fieldWithPath("data.[].distance").description("빵집까지 거리"),
                                fieldWithPath("data.[].popularNum").description("빵집 인기수")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
//    @Transactional
    void findBakeryListByFilter() throws Exception {

    }

    @Test
//    @Transactional
    void findBakery() throws Exception {
        mockMvc.perform(get("/bakery/{bakeryId}", 1L)
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("bakery/find",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호")
                        ),
                        responseFields(
                                fieldWithPath("data.info").description("빵집 정보"),
                                fieldWithPath("data.info.image").description("빵집 이미지"),
                                fieldWithPath("data.info.name").description("빵집 이름"),
                                fieldWithPath("data.info.flagNum").description("빵집 가봤어요 수"),
                                fieldWithPath("data.info.rating").description("빵집 평점"),
                                fieldWithPath("data.info.reviewNum").description("빵집 리뷰 수"),
                                fieldWithPath("data.info.domicileAddress").description("빵집 도로명 주소"),
                                fieldWithPath("data.info.streetAddress").description("빵집 번지 주소"),
                                fieldWithPath("data.info.hours").description("빵집 영업 시간"),
                                fieldWithPath("data.info.websiteURL").description("빵집 웹사이트"),
                                fieldWithPath("data.info.instagramURL").description("빵집 인스타"),
                                fieldWithPath("data.info.facebookURL").description("빵집 페이스북"),
                                fieldWithPath("data.info.blogURL").description("빵집 블로그"),
                                fieldWithPath("data.info.phoneNumber").description("빵집 전화번호"),
                                fieldWithPath("data.menu").description("빵집 메뉴"),
                                fieldWithPath("data.menu.[].id").description("빵 고유번호"),
                                fieldWithPath("data.menu.[].name").description("빵 이름"),
                                fieldWithPath("data.menu.[].rating").description("빵 평점"),
                                fieldWithPath("data.menu.[].reviewNum").description("빵 리뷰 수"),
                                fieldWithPath("data.menu.[].price").description("빵 가격"),
                                fieldWithPath("data.facilityInfoList").description("빵집 시설 정보")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void heartToBakery() throws Exception {
        mockMvc.perform(patch("/bakery/{bakeryId}/heart", 1L)
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("bakery/heart",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호")
                        )))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void unHeartToBakery() throws Exception {
        User user = User.builder().nickName("nickname2").roleType(RoleType.USER).username("username2").build();
        userRepository.save(user);
        JwtToken token = jwtTokenProvider.createJwtToken(user.getUsername(), user.getRoleType().getCode());
        user.getWantToGoList().add(bakery1.getId());

        mockMvc.perform(patch("/bakery/{bakeryId}/unheart", 1L)
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("bakery/unHeart",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호")
                        )))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void flagToBakery() throws Exception {
        mockMvc.perform(patch("/bakery/{bakeryId}/flag", 1L)
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("bakery/flag",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호")
                        )))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void unFlagToBakery() throws Exception {
        User user = User.builder().nickName("nickname2").roleType(RoleType.USER).username("username2").build();
        userRepository.save(user);
        JwtToken token = jwtTokenProvider.createJwtToken(user.getUsername(), user.getRoleType().getCode());
        user.getAlreadyGoList().add(bakery1.getId());
        bakery1.addFlagNum();

        mockMvc.perform(patch("/bakery/{bakeryId}/unflag", 1L)
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("bakery/unFlag",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(
                                parameterWithName("bakeryId").description("빵집 고유 번호")
                        )))
                .andExpect(status().isOk());
    }

}