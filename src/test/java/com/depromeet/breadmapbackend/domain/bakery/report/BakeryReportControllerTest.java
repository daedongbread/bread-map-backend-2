package com.depromeet.breadmapbackend.domain.bakery.report;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.bakery.product.Product;
import com.depromeet.breadmapbackend.domain.bakery.product.ProductType;
import com.depromeet.breadmapbackend.domain.bakery.report.dto.BakeryAddReportRequest;
import com.depromeet.breadmapbackend.domain.bakery.report.dto.BakeryUpdateReportRequest;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewProductRating;
import com.depromeet.breadmapbackend.domain.review.comment.ReviewComment;
import com.depromeet.breadmapbackend.domain.review.comment.like.ReviewCommentLike;
import com.depromeet.breadmapbackend.domain.review.like.ReviewLike;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.utils.ControllerTest;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BakeryReportControllerTest extends ControllerTest {
    private Bakery bakery;
    private JwtToken token;

    @BeforeEach
    public void setup() {
        User user = User.builder().nickName("nickname").roleType(RoleType.USER).username("username").build();
        userRepository.save(user);
        token = jwtTokenProvider.createJwtToken(user.getUsername(), user.getRoleType().getCode());

        List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);
        bakery = Bakery.builder().id(1L).address("address1").latitude(37.5596080725671).longitude(127.044235133983)
                .facilityInfoList(facilityInfo).name("bakery1").status(BakeryStatus.POSTING).build();
        bakeryRepository.save(bakery);
    }

    @AfterEach
    public void setDown() {
        bakeryUpdateReportImageRepository.deleteAllInBatch();
        bakeryUpdateReportRepository.deleteAllInBatch();
        bakeryAddReportRepository.deleteAllInBatch();
        bakeryReportImageRepository.deleteAllInBatch();
        bakeryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    void bakeryUpdateReport() throws Exception {
        String object = objectMapper.writeValueAsString(BakeryUpdateReportRequest.builder().content("newContent").build());
        MockMultipartFile request =
                new MockMultipartFile("request", "", "application/json", object.getBytes());

        mockMvc.perform(RestDocumentationRequestBuilders
                        .fileUpload("/v1/bakeries/{bakeryId}/bakery-update-reports", bakery.getId())
                        .file(new MockMultipartFile("files", UUID.randomUUID() +".png", "image/png", "test".getBytes()))
                        .file(request).accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/bakery/report/update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(parameterWithName("bakeryId").description("빵집 고유 번호")),
                        requestParts(
                                partWithName("request").description("빵집 수정 제보"),
                                partWithName("files").description("빵집 수정 이미지들")
                        ),
                        requestPartBody("request"),
                        requestPartFields("request",
//                                fieldWithPath("reason").description("빵집 수정 제보 이유"),
                                fieldWithPath("content").optional().description("수정 사항")
                        )
                ))
                .andExpect(status().isCreated());
    }

    @Test
    void bakeryAddReport() throws Exception {
        String object = objectMapper.writeValueAsString(BakeryAddReportRequest.builder()
                .name("newBakery").location("newLocation").content("newContent").build());

        mockMvc.perform(post("/v1/bakeries/bakery-add-reports")
                        .header("Authorization", "Bearer " + token.getAccessToken())
                        .content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("v1/bakery/report/add",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        requestFields(
                                fieldWithPath("name").description("제보 빵집 이름"),
                                fieldWithPath("location").description("제보 빵집 위치"),
                                fieldWithPath("content").optional().description("추천 이유")
                        )
                ))
                .andExpect(status().isCreated());
    }

    @Test
    void bakeryReportImage() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders
                        .fileUpload("/v1/bakeries/{bakeryId}/bakery-report-images", bakery.getId())
                        .file(new MockMultipartFile("files", UUID.randomUUID() +".png", "image/png", "test".getBytes()))
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/bakery/report/image",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
                        pathParameters(parameterWithName("bakeryId").description("빵집 고유 번호"))
                ))
                .andExpect(status().isCreated());
    }
}
