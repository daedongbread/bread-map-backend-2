package com.depromeet.breadmapbackend.domain.admin;

import com.depromeet.breadmapbackend.domain.admin.Admin;
import com.depromeet.breadmapbackend.domain.admin.bakery.AdminBakeryImageType;
import com.depromeet.breadmapbackend.domain.admin.bakery.dto.*;
import com.depromeet.breadmapbackend.domain.bakery.*;
import com.depromeet.breadmapbackend.domain.bakery.report.*;
import com.depromeet.breadmapbackend.global.ImageType;
import com.depromeet.breadmapbackend.domain.bakery.product.Product;
import com.depromeet.breadmapbackend.domain.bakery.product.report.ProductAddReport;
import com.depromeet.breadmapbackend.domain.bakery.product.report.ProductAddReportImage;
import com.depromeet.breadmapbackend.domain.bakery.product.ProductType;
import com.depromeet.breadmapbackend.domain.review.*;
import com.depromeet.breadmapbackend.domain.review.ReviewImage;
import com.depromeet.breadmapbackend.domain.review.report.ReviewReport;
import com.depromeet.breadmapbackend.domain.review.report.ReviewReportReason;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.utils.ControllerTest;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.domain.user.dto.ReissueRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

class AdminControllerTest extends ControllerTest {
    private JwtToken token;

    @BeforeEach
    public void setup() throws IOException {
        Admin admin = Admin.builder().email("email").password(passwordEncoder.encode("password")).build();
        adminRepository.save(admin);
        token = jwtTokenProvider.createJwtToken(admin.getEmail(), admin.getRoleType().getCode());
        redisTemplate.opsForValue()
                .set(customRedisProperties.getKey().getAdminRefresh() + ":" + admin.getId(),
                        token.getRefreshToken(), jwtTokenProvider.getRefreshTokenExpiredDate(), TimeUnit.MILLISECONDS);

        User user = User.builder().nickName("nickname").roleType(RoleType.USER).username("username").build();
        userRepository.save(user);

        List<FacilityInfo> facilityInfo = Collections.singletonList(FacilityInfo.PARKING);
        Bakery bakery = Bakery.builder().id(1L).address("address").latitude(37.5596080725671).longitude(127.044235133983)
                .facilityInfoList(facilityInfo).name("bakery").status(BakeryStatus.POSTING).build();
        bakeryRepository.save(bakery);
        bakery.updateImage(customAWSS3Properties.getCloudFront() + "/" + "bakeryImage.jpg");
        s3Uploader.upload(
                new MockMultipartFile("image", "bakeryImage.jpg", "image/jpg", "test".getBytes()),
                "bakeryImage.jpg");

        Review review = Review.builder().user(user).bakery(bakery).content("content1").build();
        reviewRepository.save(review);

        ReviewReport reviewReport = ReviewReport.builder()
                .reporter(user).review(review).reason(ReviewReportReason.COPYRIGHT_THEFT).content("content").build();
        reviewReportRepository.save(reviewReport);
    }

    @AfterEach
    public void setDown() {
        s3Uploader.deleteFileS3(customAWSS3Properties.getBucket() + "/bakeryImage.jpg");
        reviewReportRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        bakeryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        adminRepository.deleteAllInBatch();
    }

    @Test
    void adminLogin() throws Exception {
        String object = objectMapper.writeValueAsString(AdminLoginRequest.builder()
                .email("email").password("password").build());

        ResultActions result = mockMvc.perform(post("/v1/admin/login")
                .content(object)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andDo(print())
                .andDo(document("v1/admin/login",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("관리자 이메일"),
                                fieldWithPath("password").description("관리자 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("data.userId").description("유저 고유 번호"),
                                fieldWithPath("data.accessToken").description("엑세스 토큰"),
                                fieldWithPath("data.refreshToken").description("리프레시 토큰"),
                                fieldWithPath("data.accessTokenExpiredDate").description("엑세스 토큰 만료시간")
                        )
                ))
                .andExpect(status().isCreated());
    }

    @Test
//    @Transactional
    void reissue() throws Exception {
        // given
        String object = objectMapper.writeValueAsString(ReissueRequest.builder()
                .accessToken(token.getAccessToken()).refreshToken(token.getRefreshToken()).build());

        // when
        ResultActions result = mockMvc.perform(post("/v1/admin/reissue")
                .content(object)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result
                .andDo(print())
                .andDo(document("v1/admin/reissue",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("accessToken").description("엑세스 토큰"),
                                fieldWithPath("refreshToken").description("리프레시 토큰")
                        ),
                        responseFields(
                                fieldWithPath("data.userId").description("유저 고유 번호"),
                                fieldWithPath("data.accessToken").description("엑세스 토큰"),
                                fieldWithPath("data.refreshToken").description("리프레시 토큰"),
                                fieldWithPath("data.accessTokenExpiredDate").description("엑세스 토큰 만료시간")
                        )
                ))
                .andExpect(status().isCreated());
    }

    @Test
    void getAdminBar() throws Exception {
        mockMvc.perform(get("/v1/admin/bar")
                .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/admin/bar",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        responseFields(
                                fieldWithPath("data.bakeryAddReportCount").description("`검토전`인 빵집 추가 제보 수"),
                                fieldWithPath("data.bakeryCount").description("`미게시`인 빵집 수"),
                                fieldWithPath("data.reviewReportCount").description("숨김 처리하지 않은 리뷰 신고 수")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void downloadAdminImage() throws Exception {
        mockMvc.perform(get("/v1/admin/images?image=bakeryImage.jpg")
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/admin/image/download",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        requestParameters(
                                parameterWithName("image").optional().description("다운로드할 이미지 경로"))
                ))
                .andExpect(status().isOk());
    }

    @Test
    void uploadImage() throws Exception {
        mockMvc.perform(multipart("/v1/admin/images")
                        .file(new MockMultipartFile("image", UUID.randomUUID() +".png", "image/png", "test".getBytes()))
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/admin/tempImage",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        requestParts(
                                partWithName("image").description("업로드 이미지")),
                        responseFields(
                                fieldWithPath("data.imagePath").description("업로드된 이미지"))
                ))
                .andExpect(status().isCreated());
    }
}
