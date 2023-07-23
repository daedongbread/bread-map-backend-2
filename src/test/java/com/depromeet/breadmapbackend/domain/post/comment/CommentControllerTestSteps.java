package com.depromeet.breadmapbackend.domain.post.comment;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.depromeet.breadmapbackend.domain.post.comment.dto.request.CommentCreateRequest;
import com.depromeet.breadmapbackend.domain.post.comment.dto.request.CommentUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * PostControllerTestSteps
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/23
 */
public class CommentControllerTestSteps {

	public static ResultActions 댓글_추가_요청(final CommentCreateRequest 댓글_데이터, final String 사용자_토큰, final MockMvc mockMvc,
		final ObjectMapper objectMapper) throws Exception {
		final String request = objectMapper.writeValueAsString(댓글_데이터);
		return mockMvc.perform(post("/v1/comments")
				.content(request).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + 사용자_토큰))
			.andDo(print())
			.andDo(document("v1/comments/add",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
				requestFields(
					fieldWithPath("postId").description("댓글 추가 할 커뮤니티글 id"),
					fieldWithPath("content").description("댓글 내용"),
					fieldWithPath("isFirstDepth").description("댓글 = true, 대댓글 = false"),
					fieldWithPath("parentId").description("댓글일 경우 = 0 ,대댓글일 경우 부모 댓글 id")
				)
			));
	}

	public static ResultActions 빵_이야기_댓글_조회_요청(final Long 빵_이야기_고유_번호, final int page, final String 사용자_토큰,
		final MockMvc mockMvc) throws
		Exception {
		return mockMvc.perform(
				get("/v1/comments/{postId}/{page}", 빵_이야기_고유_번호, page)
					.header("Authorization", "Bearer " + 사용자_토큰))
			.andDo(print())
			.andDo(document("v1/comments/get",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
				responseFields(
					fieldWithPath("data.pageNumber").description("현재 페이지 (0부터 시작)"),
					fieldWithPath("data.numberOfElements").description("현재 페이지 데이터 수"),
					fieldWithPath("data.size").description("페이지 크기"),
					fieldWithPath("data.totalElements").description("전체 데이터 수"),
					fieldWithPath("data.totalPages").description("전체 페이지 수"),
					fieldWithPath("data.contents").description("커뮤니티 카드 리스트"),
					fieldWithPath("data.contents.[].id").description("댓글 id"),
					fieldWithPath("data.contents.[].content").description("댓글 내용"),
					fieldWithPath("data.contents.[].isFirstDepth").description("댓글 여부, 댓글 = true, 대댓글 = false"),
					fieldWithPath("data.contents.[].parentId").description("부모 댓글 ID, 댓글일 경우 = 0"),
					fieldWithPath("data.contents.[].userId").description("댓글 작성자 id"),
					fieldWithPath("data.contents.[].nickname").description("댓글 작성자 nickname"),
					fieldWithPath("data.contents.[].profileImage").description("댓글 작성자 프로필 이미지"),
					fieldWithPath("data.contents.[].likeCount").description("댓글 좋아요 개수"),
					fieldWithPath("data.contents.[].createdDate").description("댓글 작성 일자")
				)
			));
	}

	public static ResultActions 댓글_수정_요청(final CommentUpdateRequest 댓글_수정_요청_데이터, final String 사용자_토큰,
		final MockMvc mockMvc, final ObjectMapper objectMapper) throws Exception {
		final String request = objectMapper.writeValueAsString(댓글_수정_요청_데이터);
		return mockMvc.perform(
				put("/v1/comments")
					.content(request).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + 사용자_토큰))
			.andDo(print())
			.andDo(document("v1/comments/update",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
				requestFields(
					fieldWithPath("commentId").description("댓글 id"),
					fieldWithPath("content").description("댓글 내용")
				)

			));
	}

	public static ResultActions 댓글_삭제_요청(final Long 삭제할_댓글_id, final String 사용자_토큰, final MockMvc mockMvc) throws
		Exception {
		return mockMvc.perform(delete("/v1/comments/{commentId}", 삭제할_댓글_id)
				.header("Authorization", "Bearer " + 사용자_토큰))
			.andDo(print())
			.andDo(document("v1/comments/delete",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
				pathParameters(
					parameterWithName("commentId").description("삭제할 댓글 id"))
			));
	}

}
