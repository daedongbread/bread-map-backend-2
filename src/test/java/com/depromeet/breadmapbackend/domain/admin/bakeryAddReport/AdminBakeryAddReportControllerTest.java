package com.depromeet.breadmapbackend.domain.admin.bakeryAddReport;

import com.depromeet.breadmapbackend.domain.admin.Admin;
import com.depromeet.breadmapbackend.domain.admin.bakery.dto.BakeryReportStatusUpdateRequest;
import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.bakery.FacilityInfo;
import com.depromeet.breadmapbackend.domain.bakery.product.Product;
import com.depromeet.breadmapbackend.domain.bakery.product.ProductType;
import com.depromeet.breadmapbackend.domain.bakery.product.report.ProductAddReport;
import com.depromeet.breadmapbackend.domain.bakery.product.report.ProductAddReportImage;
import com.depromeet.breadmapbackend.domain.bakery.report.*;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewImage;
import com.depromeet.breadmapbackend.domain.review.ReviewProductRating;
import com.depromeet.breadmapbackend.domain.review.report.ReviewReport;
import com.depromeet.breadmapbackend.domain.review.report.ReviewReportReason;
import com.depromeet.breadmapbackend.domain.user.OAuthInfo;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserInfo;
import com.depromeet.breadmapbackend.global.ImageType;
import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.utils.ControllerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminBakeryAddReportControllerTest extends ControllerTest {
    private BakeryAddReport bakeryAddReport;
    private JwtToken token;

    @BeforeEach
    public void setup() throws IOException {
        Admin admin = Admin.builder().email("email").password(passwordEncoder.encode("password")).build();
        adminRepository.save(admin);
        token = jwtTokenProvider.createJwtToken(admin.getEmail(), admin.getRoleType().getCode());

        User user = User.builder().oAuthInfo(OAuthInfo.builder().oAuthType(OAuthType.GOOGLE).oAuthId("oAuthId1").build())
                .userInfo(UserInfo.builder().nickName("nickname1").build()).build();
        userRepository.save(user);

        bakeryAddReport = BakeryAddReport.builder().user(user).content("test content").location("test location")
                .name("test Report").build();
        bakeryAddReportRepository.save(bakeryAddReport);
    }

    @AfterEach
    public void setDown() {
        bakeryAddReportRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        adminRepository.deleteAllInBatch();
    }

    @Test
    void getBakeryAddReportList() throws Exception {
        mockMvc.perform(get("/v1/admin/bakery-add-reports?page=0")
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/admin/bakeryReport/all",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호")),
                        responseFields(
                                fieldWithPath("data.pageNumber").description("현재 페이지 (0부터 시작)"),
                                fieldWithPath("data.numberOfElements").description("현재 페이지 데이터 수"),
                                fieldWithPath("data.size").description("페이지 크기"),
                                fieldWithPath("data.totalElements").description("전체 데이터 수"),
                                fieldWithPath("data.totalPages").description("전체 페이지 수"),
                                fieldWithPath("data.contents").description("빵집 제보 리스트"),
                                fieldWithPath("data.contents.[].reportId").description("빵집 제보 고유 번호"),
                                fieldWithPath("data.contents.[].nickName").description("빵집 제보 유저 닉네임"),
                                fieldWithPath("data.contents.[].bakeryName").description("빵집 이름"),
                                fieldWithPath("data.contents.[].location").description("빵집 위치"),
                                fieldWithPath("data.contents.[].content").description("빵집 제보 내용"),
                                fieldWithPath("data.contents.[].createdAt").description("빵집 제보 시간"),
                                fieldWithPath("data.contents.[].status")
                                        .description("빵집 제보 처리 상태 " +
                                                "(BEFORE_REFLECT(\"검토전\"),\n" +
                                                "NOT_REFLECT(\"미반영\"),\n" +
                                                "REFLECT(\"반영완료\"))")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void getBakeryAddReport() throws Exception {
        mockMvc.perform(get("/v1/admin/bakery-add-reports/{reportId}", bakeryAddReport.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/admin/bakeryReport",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(
                                parameterWithName("reportId").description("빵집 고유 번호")),
                        responseFields(
                                fieldWithPath("data.nickName").description("빵집 제보 유저 닉네임"),
                                fieldWithPath("data.bakeryName").description("빵집 이름"),
                                fieldWithPath("data.location").description("빵집 위치"),
                                fieldWithPath("data.content").description("빵집 제보 내용"),
                                fieldWithPath("data.status")
                                        .description("빵집 제보 처리 상태 " +
                                                "(BEFORE_REFLECT(\"검토전\"),\n" +
                                                "NOT_REFLECT(\"미반영\"),\n" +
                                                "REFLECT(\"반영완료\"))")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void updateBakeryAddReportStatus() throws Exception {
        String object = objectMapper.writeValueAsString(BakeryReportStatusUpdateRequest.builder()
                .status(BakeryAddReportStatus.REFLECT).build());

        mockMvc.perform(patch("/v1/admin/bakery-add-reports/{reportId}", bakeryAddReport.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken())
                        .content(object).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("v1/admin/bakeryReport/update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(
                                parameterWithName("reportId").description("빵집 제보 고유 번호")),
                        requestFields(
                                fieldWithPath("status").description("빵집 제보 처리 상태 " +
                                        "(BEFORE_REFLECT(\"검토전\"),\n" +
                                        "NOT_REFLECT(\"미반영\"),\n" +
                                        "REFLECT(\"반영완료\"))")
                        )
                ))
                .andExpect(status().isNoContent());
    }
}
