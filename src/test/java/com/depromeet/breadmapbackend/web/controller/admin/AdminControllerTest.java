package com.depromeet.breadmapbackend.web.controller.admin;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.Bread;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.review.BreadRating;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.security.domain.RoleType;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.utils.ControllerTest;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

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

public class AdminControllerTest extends ControllerTest {
    private User user;
    private Bakery bakery1;
    private Bakery bakery2;
    private JwtToken token;

    @BeforeEach
    public void setup() {
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

        user = User.builder().nickName("nickname").roleType(RoleType.USER).username("username").build();
        userRepository.save(user);
        token = jwtTokenProvider.createJwtToken(user.getUsername(), user.getRoleType().getCode());

        bakery1 = Bakery.builder().id(1L).domicileAddress("domicile").latitude(37.5596080725671).longitude(127.044235133983)
                .name("bakery1").streetAddress("street").build();
        bakery2 = Bakery.builder().id(2L).domicileAddress("domicile").latitude(37.55950448505721).longitude(127.04416263787213)
                .name("bakery2").streetAddress("street").build();
        bakery1.addFacilityInfo(FacilityInfo.PARKING);
        bakery1.addFacilityInfo(FacilityInfo.DELIVERY);

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
                                fieldWithPath("data.[].id").description("빵집 id"),
                                fieldWithPath("data.[].isUse").description("빵집 게시현황"),
                                fieldWithPath("data.[].name").description("빵집 이름"),
                                fieldWithPath("data.[].createdAt").description("빵집 생성 일자"),
                                fieldWithPath("data.[].modifiedAt").description("빵집 수정일자")
                        )
                ))
                .andExpect(status().isOk());
    }
}
