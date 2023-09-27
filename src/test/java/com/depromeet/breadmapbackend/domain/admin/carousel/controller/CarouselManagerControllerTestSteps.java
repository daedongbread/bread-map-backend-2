package com.depromeet.breadmapbackend.domain.admin.carousel.controller;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import com.depromeet.breadmapbackend.domain.admin.carousel.domain.dto.command.UpdateCarouselOrderCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/**
 * PostAdminControllerTestSteps
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/27
 */
public class CarouselManagerControllerTestSteps {

    public static ResultActions 이벤트_순서_수정_요청(
        final List<UpdateCarouselOrderCommand> 이벤트_순서_수정_요청_데이터,
        final String 관리자_토큰,
        final MockMvc mockMvc,
        final ObjectMapper objectMapper
    ) throws Exception {
        final String request = objectMapper.writeValueAsString(이벤트_순서_수정_요청_데이터);
        return mockMvc.perform(patch("/v1/admin/carousels/order")
                .content(request).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + 관리자_토큰))
            .andDo(print())
            .andDo(document("v1/admin/posts/order/update",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                requestFields(
                    fieldWithPath("[].order").description("정렬 순서"),
                    fieldWithPath("[].id").description("이벤트 id")
                )
            ));
    }

    public static ResultActions 이벤트_캐러셀_조회_요청(
        final String 관리자_토큰,
        final MockMvc mockMvc
    ) throws Exception {
        return mockMvc.perform(get("/v1/admin/carousels")
                .header("Authorization", "Bearer " + 관리자_토큰))
            .andDo(print())
            .andDo(document("v1/admin/carousels",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                responseFields(
                    fieldWithPath("data.[].managerId").description("이벤트 id"),
                    fieldWithPath("data.[].order").description("이밴트 캐러셀 순서"),
                    fieldWithPath("data.[].bannerImage").description("이밴트 캐러셀 배너 이미지")
                )
            ));
    }

}