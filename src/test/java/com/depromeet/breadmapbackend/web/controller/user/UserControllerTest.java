package com.depromeet.breadmapbackend.web.controller.user;

import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.restdocs.utils.ControllerTest;
import com.depromeet.breadmapbackend.security.domain.RoleType;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.depromeet.breadmapbackend.security.token.RefreshToken;
import com.depromeet.breadmapbackend.web.controller.user.dto.TokenRefreshRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends ControllerTest {
    private User user;
    private JwtToken token;

    @BeforeEach
    public void setUp() {
        user = User.builder().username("testUserName").nickName("testNickName").roleType(RoleType.USER).build();
        userRepository.save(user);
        token = jwtTokenProvider.createJwtToken(user.getUsername(), RoleType.USER.getCode());
        refreshTokenRepository.save(RefreshToken.builder().username(user.getUsername()).token(token.getRefreshToken()).build());
    }

    @Test
    @Transactional
    void refresh() throws Exception {
        // given
        String object = objectMapper.writeValueAsString(TokenRefreshRequest.builder()
                .accessToken(token.getAccessToken()).refreshToken(token.getRefreshToken()).build());

        // when
        ResultActions result = mockMvc.perform(post("/user/auth/reissue")
                .content(object)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result
                .andDo(print())
                .andDo(document("user/reissue",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("엑세스 토큰"),
                                fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰")
                        ),
                        responseFields(
                                fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("엑세스 토큰"),
                                fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰"),
                                fieldWithPath("data.accessTokenExpiredDate").type(JsonFieldType.NUMBER).description("엑세스 토큰 만료시간")
                        )
                ))
                .andExpect(status().isCreated());
    }
}
