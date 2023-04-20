package com.depromeet.breadmapbackend.domain.image;

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
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ImageControllerTest extends ControllerTest {
    private JwtToken token;


    @BeforeEach
    public void setup() {
        User user = User.builder().oAuthInfo(OAuthInfo.builder().oAuthType(OAuthType.GOOGLE).oAuthId("oAuthId1").build())
                .userInfo(UserInfo.builder().nickName("nickname1").build()).build();
        userRepository.save(user);
        token = jwtTokenProvider.createJwtToken(user.getOAuthId(), user.getRoleType().getCode());
    }


    @AfterEach
    public void setDown() {
        userRepository.deleteAllInBatch();
    }

    @Test
    void uploadImage() throws Exception {
        mockMvc.perform(multipart("/v1/images")
                        .file(new MockMultipartFile("image", UUID.randomUUID() +".png", "image/png", "test".getBytes()))
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/image",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저/관리자의 Access Token")),
                        requestParts(
                                partWithName("image").description("업로드 이미지")),
                        responseFields(
                                fieldWithPath("data.imagePath").description("업로드된 이미지 경로"))
                ))
                .andExpect(status().isCreated());
    }

    @Test
    void uploadImages() throws Exception {
        mockMvc.perform(multipart("/v1/images/multi")
                        .file(new MockMultipartFile("images", UUID.randomUUID() +".png", "image/png", "test1".getBytes()))
                        .file(new MockMultipartFile("images", UUID.randomUUID() +".png", "image/png", "test2".getBytes()))
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andDo(print())
                .andDo(document("v1/image/multi",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName("Authorization").description("유저/관리자의 Access Token")),
                        requestParts(
                                partWithName("images").description("업로드 이미지들")),
                        responseFields(
                                fieldWithPath("data.[].imagePath").description("업로드된 이미지들 경로"))
                ))
                .andExpect(status().isCreated());
    }
}