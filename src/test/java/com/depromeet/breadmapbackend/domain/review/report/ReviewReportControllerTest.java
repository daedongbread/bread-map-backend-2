package com.depromeet.breadmapbackend.domain.review.report;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.report.dto.ReviewReportRequest;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.utils.ControllerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReviewReportControllerTest extends ControllerTest {
    private JwtToken token;
    private Review review;

    @BeforeEach
    public void setup() {
        User user1 = User.builder().nickName("nickname1").roleType(RoleType.USER).username("username1").build();
        User user2 = User.builder().nickName("nickname2").roleType(RoleType.USER).username("username2").build();
        userRepository.save(user1);
        userRepository.save(user2);
        token = jwtTokenProvider.createJwtToken(user1.getUsername(), user1.getRoleType().getCode());

        List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);
        Bakery bakery = Bakery.builder().id(1L).address("address").latitude(37.5596080725671).longitude(127.044235133983)
                .facilityInfoList(facilityInfo).name("bakery1").status(BakeryStatus.POSTING).build();
        bakeryRepository.save(bakery);

        review = Review.builder().user(user2).bakery(bakery).content("content1").build();
        reviewRepository.save(review);
    }

    @AfterEach
    public void setDown() {
        reviewReportRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    void reviewReport() throws Exception {
        String object = objectMapper.writeValueAsString(
                ReviewReportRequest.builder().reason(ReviewReportReason.COPYRIGHT_THEFT).build());

        mockMvc.perform(post("/v1/reviews/{reviewId}/report", review.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken())
                        .content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("v1/review/report",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(parameterWithName("reviewId").description("리뷰 고유 번호")),
                        requestFields(
                                fieldWithPath("reason").description("리뷰 신고 이유"),
                                fieldWithPath("content").optional().description("리뷰 신고 내용")
                        )
                ))
                .andExpect(status().isCreated());
    }
}
