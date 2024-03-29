package com.depromeet.breadmapbackend.domain.admin.post.controller;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import com.depromeet.breadmapbackend.domain.admin.post.domain.dto.command.EventCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class PostAdminControllerTestSteps {

    public static ResultActions 이벤트_조회_요청(final String 관리자_토큰, final MockMvc mockMvc) throws Exception {
        return mockMvc.perform(get("/v1/admin/posts/{page}", 0)
                .header("Authorization", "Bearer " + 관리자_토큰))
            .andDo(print())
            .andDo(document("v1/admin/posts",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                pathParameters(
                    parameterWithName("page").description("페이지 번호 (0부터 시작)")
                ),
                responseFields(
                    fieldWithPath("data.pageNumber").description("현재 페이지 (0부터 시작)"),
                    fieldWithPath("data.numberOfElements").description("현재 페이지 데이터 수"),
                    fieldWithPath("data.size").description("페이지 크기"),
                    fieldWithPath("data.totalElements").description("전체 데이터 수"),
                    fieldWithPath("data.totalPages").description("전체 페이지 수"),
                    fieldWithPath("data.contents").description("등록된 이벤트 리스트"),
                    fieldWithPath("data.contents.[].managerId").description("이벤트 id"),
                    fieldWithPath("data.contents.[].nickname").description("이벤트 작성자 닉네임"),
                    fieldWithPath("data.contents.[].userId").description("이벤트 작성자 id"),
                    fieldWithPath("data.contents.[].title").description("이벤트 제목"),
                    fieldWithPath("data.contents.[].isFixed").description("고정 여부"),
                    fieldWithPath("data.contents.[].isCarousel").description("캐러셀 여부"),
                    fieldWithPath("data.contents.[].isPosted").description("게시 여부"),
                    fieldWithPath("data.contents.[].createdAt").description("등록 일자")
                )
            ));
    }

    public static ResultActions 이벤트_등록_요청(
        final EventCommand 이벤트_등록_요청_데이터,
        final String 관리자_토큰,
        final MockMvc mockMvc,
        final ObjectMapper objectMapper
    ) throws Exception {
        final String request = objectMapper.writeValueAsString(이벤트_등록_요청_데이터);
        return mockMvc.perform(post("/v1/admin/posts")
                .content(request).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + 관리자_토큰))
            .andDo(print())
            .andDo(document("v1/admin/posts/add",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                requestFields(
                    fieldWithPath("isPosted").optional().description("게시 여부"),
                    fieldWithPath("isFixed").optional().description("고정 여부"),
                    fieldWithPath("isCarousel").optional().description("캐러셀 여부"),
                    fieldWithPath("title").description("이벤트 제목 (10자 이상 40자 이하)"),
                    fieldWithPath("content").description("이벤트 내용 (10자 이상 400자 이하)"),
                    fieldWithPath("bannerImage").description("이벤트 베너 이미지"),
                    fieldWithPath("images").description("이벤트 컨텐츠 이미지 리스")
                ),
                responseFields(
                    fieldWithPath("data").description("등록된 이벤트 id")
                )
            ));
    }

    public static ResultActions 이벤트_고정_가능여부_조회_요청(
        final String 관리자_토큰,
        final MockMvc mockMvc
    ) throws Exception {
        return mockMvc.perform(
                get("/v1/admin/posts/can-fix")
                    .header("Authorization", "Bearer " + 관리자_토큰))
            .andDo(print())
            .andDo(document("v1/admin/posts/can-fix",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                responseFields(
                    fieldWithPath("data").description("이벤트 게시글 고정 가능 여부")
                )
            ));
    }

    public static ResultActions 이벤트_수정_요청(
        final EventCommand 이벤트_수정_요청_데이터,
        final Long 이벤트_고유_번호,
        final String 관리자_토큰,
        final MockMvc mockMvc,
        final ObjectMapper objectMapper
    ) throws Exception {
        final String request = objectMapper.writeValueAsString(이벤트_수정_요청_데이터);
        return mockMvc.perform(patch("/v1/admin/posts/{managerId}", 이벤트_고유_번호)
                .content(request).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + 관리자_토큰))
            .andDo(print())
            .andDo(document("v1/admin/posts/update",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                pathParameters(
                    parameterWithName("managerId").description("이벤트 id")
                ),
                requestFields(
                    fieldWithPath("isPosted").description("게시 여부"),
                    fieldWithPath("isFixed").description("고정 여부"),
                    fieldWithPath("isCarousel").description("캐러셀 여부"),
                    fieldWithPath("title").description("이벤트 제목"),
                    fieldWithPath("content").description("이벤트 내용"),
                    fieldWithPath("bannerImage").description("이벤트 베너 이미지"),
                    fieldWithPath("images").description("이벤트 컨텐츠 이미지 리스트")
                )
            ));
    }


    public static ResultActions 이벤트_상세_조회_요청(
        final Long 이벤트_고유_번호,
        final String 관리자_토큰,
        final MockMvc mockMvc
    ) throws Exception {
        return mockMvc.perform(get("/v1/admin/posts/detail/{managerId}", 이벤트_고유_번호)
                .header("Authorization", "Bearer " + 관리자_토큰))
            .andDo(print())
            .andDo(document("v1/admin/posts/get",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
                pathParameters(
                    parameterWithName("managerId").description("조회 요청 이벤트 id")
                ),
                responseFields(
                    fieldWithPath("managerId").description("이밴트 포스트 관리 id"),
                    fieldWithPath("isPosted").description("게시 여부"),
                    fieldWithPath("isFixed").description("고정 여부"),
                    fieldWithPath("isCarousel").description("캐러셀 여부"),
                    fieldWithPath("title").description("이벤트 제목"),
                    fieldWithPath("content").description("이벤트 내용"),
                    fieldWithPath("bannerImage").description("이벤트 베너 이미지"),
                    fieldWithPath("images").description("이벤트 컨텐츠 이미지 리스트")
                )));

    }
}