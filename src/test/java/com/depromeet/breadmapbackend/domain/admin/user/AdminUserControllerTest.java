package com.depromeet.breadmapbackend.domain.admin.user;

import com.depromeet.breadmapbackend.domain.admin.Admin;
import com.depromeet.breadmapbackend.domain.user.OAuthInfo;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserInfo;
import com.depromeet.breadmapbackend.global.security.domain.OAuthType;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.depromeet.breadmapbackend.global.security.token.JwtToken;
import com.depromeet.breadmapbackend.utils.ControllerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminUserControllerTest extends ControllerTest {
    private JwtToken token;
    private User user;

    @BeforeEach
    public void setup() throws IOException {
        Admin admin = Admin.builder().email("email").password(passwordEncoder.encode("password")).build();
        adminRepository.save(admin);
        token = jwtTokenProvider.createJwtToken(admin.getEmail(), admin.getRoleType().getCode());

        user = User.builder().oAuthInfo(OAuthInfo.builder().oAuthType(OAuthType.GOOGLE).oAuthId("oAuthId1").build())
                .userInfo(UserInfo.builder().nickName("nickname1").build()).build();
        userRepository.save(user);
    }

    @AfterEach
    public void setDown() {
        userRepository.deleteAllInBatch();
        adminRepository.deleteAllInBatch();
    }

    @Test
    void getUserList() throws Exception {
        mockMvc.perform(get("/v1/admin/users?page=0")
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/admin/user/all",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        requestParameters(
                                parameterWithName("page").description("페이지 숫자")),
                        responseFields(
                                fieldWithPath("data.pageNumber").description("현재 페이지 (0부터 시작)"),
                                fieldWithPath("data.numberOfElements").description("현재 페이지 데이터 수"),
                                fieldWithPath("data.size").description("페이지 크기"),
                                fieldWithPath("data.totalElements").description("전체 데이터 수"),
                                fieldWithPath("data.totalPages").description("전체 페이지 수"),
                                fieldWithPath("data.contents").description("유저 리스트"),
                                fieldWithPath("data.contents.[].userId").description("유저 고유 번호"),
                                fieldWithPath("data.contents.[].userOAuthId").description("유저 식별자"),
                                fieldWithPath("data.contents.[].nickName").description("유저 닉네임"),
                                fieldWithPath("data.contents.[].email").description("유저 이메일").optional(),
                                fieldWithPath("data.contents.[].createdAt").description("유저 가입 날짜"),
                                fieldWithPath("data.contents.[].lastAccessAt").description("유저 최종 접속"),
                                fieldWithPath("data.contents.[].roleType").description("유저 권한")
                        )
                ))
                .andExpect(status().isOk());
    }

    @Test
    void changeUserBlock() throws Exception {
        mockMvc.perform(patch("/v1/admin/users/{userId}/block", user.getId())
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/admin/user/block",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                        pathParameters(
                                parameterWithName("userId").description("차단 유저 고유 번호"))
                ))
                .andExpect(status().isNoContent());
    }
}
