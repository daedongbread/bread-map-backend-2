package com.depromeet.breadmapbackend.restdocs;

import com.depromeet.breadmapbackend.restdocs.utils.ApiDocumentationTest;
import com.depromeet.breadmapbackend.security.domain.RoleType;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.web.controller.user.dto.TokenRefreshRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static com.depromeet.breadmapbackend.restdocs.utils.ApiDocumentUtils.getDocumentRequest;
import static com.depromeet.breadmapbackend.restdocs.utils.ApiDocumentUtils.getDocumentResponse;
import static com.depromeet.breadmapbackend.security.util.HeaderConstant.HEADER_ACCESS_TOKEN_PREFIX;
import static com.depromeet.breadmapbackend.security.util.HeaderConstant.HEADER_AUTHORIZATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserDocumentationTest extends ApiDocumentationTest {

    @Test
    public void refresh() throws Exception {
        // given
        String username = "abc123";

        JwtToken originJwtToken = tokenProvider.createJwtToken(username, RoleType.USER.getCode());
        TokenRefreshRequest request = new TokenRefreshRequest();
        request.setRefreshToken(originJwtToken.getRefreshToken());

        JwtToken newJwtToken = tokenProvider.createJwtToken(username, RoleType.USER.getCode());
        given(userService.refresh(any()))
                .willReturn(newJwtToken);

        // when
        ResultActions result = mockMvc.perform(
                get("/user/auth/refresh")
                        .header(HEADER_AUTHORIZATION, HEADER_ACCESS_TOKEN_PREFIX + originJwtToken.getAccessToken())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        result
                .andDo(document("user-auth-refresh",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HEADER_AUTHORIZATION).description("엑세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰")
                        ),
                        responseFields(
                                fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("엑세스 토큰"),
                                fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰")
                        )
                ))
                .andExpect(status().isOk());
    }

}
