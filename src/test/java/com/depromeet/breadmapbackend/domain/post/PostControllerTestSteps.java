package com.depromeet.breadmapbackend.domain.post;

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

import com.depromeet.breadmapbackend.domain.post.dto.request.PostRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * PostControllerTestSteps
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/23
 */
public class PostControllerTestSteps {

	public static ResultActions 빵_이야기_작성_요청(final PostRequest 글_작성_데이터, final String 사용자_토큰, final MockMvc mockMvc,
		final ObjectMapper objectMapper) throws Exception {
		final String request = objectMapper.writeValueAsString(글_작성_데이터);
		return mockMvc.perform(post("/v1/posts")
				.content(request).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + 사용자_토큰))
			.andDo(print())
			.andDo(document("v1/posts/add",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("유저의 Access Token")),
				requestFields(
					fieldWithPath("title").description("빵 이야기 제목"),
					fieldWithPath("content").description("빵 이야기 내용"),
					fieldWithPath("images").optional().description("빵 이야기 첨부 이미지")
				)
			));
	}

	public static ResultActions 커뮤니티_추천_조회_요청(final String 사용자_토큰, final MockMvc mockMvc) throws
		Exception {
		return mockMvc.perform(
				get("/v1/posts/hot")
					.header("Authorization", "Bearer " + 사용자_토큰))
			.andDo(print())
			.andDo(document("v1/posts/get/hot",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
				responseFields(
					fieldWithPath("data.[].writerInfo").description("커뮤니티 작성자 정보"),
					fieldWithPath("data.[].writerInfo.userId").description("커뮤니티 작성자 고유 번호"),
					fieldWithPath("data.[].writerInfo.nickname").description("커뮤니티 작성자 닉네임"),
					fieldWithPath("data.[].writerInfo.profileImage").description("커뮤니티 작성자 프로필 이미지"),
					fieldWithPath("data.[].postId").description("커뮤니티 고유 번호"),
					fieldWithPath("data.[].title").optional().description("커뮤니티 제목, 빵집의 경우 없음"),
					fieldWithPath("data.[].content").description("커뮤니티 내용"),
					fieldWithPath("data.[].likeCount").description("커뮤니티 좋아요 개수"),
					fieldWithPath("data.[].commentCount").description("커뮤니티 댓글 개수"),
					fieldWithPath("data.[].thumbnail").optional().description("커뮤니티 글 썸네일"),
					fieldWithPath("data.[].postTopic").description("커뮤니티 타입 (BREAD_STORY, EVENT, REVIEW)"),
					fieldWithPath("data.[].createdDate").description("커뮤니티 작성 일자"),
					fieldWithPath("data.[].bakeryInfo").optional().description("postTopic이 REVIEW일 경우 빵집 정보"),
					fieldWithPath("data.[].bakeryInfo.bakeryId").optional()
						.description("postTopic이 REVIEW일 경우 빵집 고유 번호"),
					fieldWithPath("data.[].bakeryInfo.name").optional()
						.description("postTopic이 REVIEW일 경우 빵집 이름"),
					fieldWithPath("data.[].bakeryInfo.address").optional()
						.description("postTopic이 REVIEW일 경우 빵집 주소"),
					fieldWithPath("data.[].bakeryInfo.thumbnail").optional()
						.description("postTopic이 REVIEW일 경우 빵집 썸네일")
				)
			));
	}

	public static ResultActions 커뮤니티_전체_카테고리_조회_요청(final CommunityPage 커뮤니티_조회_페이지_데이터, final String 사용자_토큰,
		final MockMvc mockMvc) throws
		Exception {
		return mockMvc.perform(
				get("/v1/posts/cards/{all}?reviewOffset={reviewOffset}&postOffset={postOffset}&page={page}",
					"all", 커뮤니티_조회_페이지_데이터.reviewOffset(), 커뮤니티_조회_페이지_데이터.postOffset(), 커뮤니티_조회_페이지_데이터.page())
					.header("Authorization", "Bearer " + 사용자_토큰))
			.andDo(print())
			.andDo(document("v1/posts/get/all",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
				pathParameters(
					parameterWithName("all").description("커뮤니티 토픽 종류 (all, review, bread-story, event")),
				requestParameters(
					parameterWithName("reviewOffset").description("마지막 조회 리뷰 고유 번호"),
					parameterWithName("postOffset").description("리뷰 제외 모든 커뮤니티글의 마지 고유 번호"),
					parameterWithName("page").description("페이지 번호")),
				responseFields(
					fieldWithPath("data.pageNumber").description("현재 페이지 (0부터 시작)"),
					fieldWithPath("data.numberOfElements").description("현재 페이지 데이터 수"),
					fieldWithPath("data.size").description("페이지 크기"),
					fieldWithPath("data.totalElements").description("전체 데이터 수"),
					fieldWithPath("data.totalPages").description("전체 페이지 수"),
					fieldWithPath("data.postOffset").description("리뷰 제외 커뮤니티 조회 offset"),
					fieldWithPath("data.reviewOffset").description("리뷰 조회 offset"),
					fieldWithPath("data.contents").description("커뮤니티 카드 리스트"),
					fieldWithPath("data.contents.[].writerInfo").description("커뮤니티 작성자 정보"),
					fieldWithPath("data.contents.[].writerInfo.userId").description("커뮤니티 작성자 고유 번호"),
					fieldWithPath("data.contents.[].writerInfo.nickname").description("커뮤니티 작성자 닉네임"),
					fieldWithPath("data.contents.[].writerInfo.profileImage").description("커뮤니티 작성자 프로필 이미지"),
					fieldWithPath("data.contents.[].postId").description("커뮤니티 고유 번호"),
					fieldWithPath("data.contents.[].title").optional().description("커뮤니티 제목, 빵집의 경우 없음"),
					fieldWithPath("data.contents.[].content").description("커뮤니티 내용"),
					fieldWithPath("data.contents.[].likeCount").description("커뮤니티 좋아요 개수"),
					fieldWithPath("data.contents.[].commentCount").description("커뮤니티 댓글 개수"),
					fieldWithPath("data.contents.[].thumbnail").optional().description("커뮤니티 글 썸네일"),
					fieldWithPath("data.contents.[].postTopic").description("커뮤니티 타입 (BREAD_STORY, EVENT, REVIEW)"),
					fieldWithPath("data.contents.[].createdDate").description("커뮤니티 작성 일자"),
					fieldWithPath("data.contents.[].bakeryInfo").optional().description("postTopic이 REVIEW일 경우 빵집 정보"),
					fieldWithPath("data.contents.[].bakeryInfo.bakeryId").optional()
						.description("postTopic이 REVIEW일 경우 빵집 고유 번호"),
					fieldWithPath("data.contents.[].bakeryInfo.name").optional()
						.description("postTopic이 REVIEW일 경우 빵집 이름"),
					fieldWithPath("data.contents.[].bakeryInfo.address").optional()
						.description("postTopic이 REVIEW일 경우 빵집 주소"),
					fieldWithPath("data.contents.[].bakeryInfo.thumbnail").optional()
						.description("postTopic이 REVIEW일 경우 빵집 썸네일")

				)
			));
	}

	public static ResultActions 빵_이야기_상세_조회_요청(final int 빵_이야기_고유_번호, final PostTopic postTopic, final String 사용자_토큰,
		final MockMvc mockMvc) throws
		Exception {
		return mockMvc.perform(get("/v1/posts/{postId}/{postTopic}", 빵_이야기_고유_번호, postTopic.getTopic())
				.header("Authorization", "Bearer " + 사용자_토큰))
			.andDo(print())
			.andDo(document("v1/posts/get",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(headerWithName("Authorization").description("관리자의 Access Token")),
				pathParameters(
					parameterWithName("postId").description("빵 이야기 고유 번호"),
					parameterWithName("postTopic").description("커뮤니티 토픽")),
				responseFields(
					fieldWithPath("data.postId").description("빵 이야기 고유 번호"),
					fieldWithPath("data.postTopic").description("커뮤니티 타입 (BREAD_STORY, EVENT, REVIEW)"),
					fieldWithPath("data.title").description("빵 이야기 제목"),
					fieldWithPath("data.writerInfo").description("빵 이야기 작성자 정보"),
					fieldWithPath("data.writerInfo.userId").description("빵 이야기 작성자 고유 번호"),
					fieldWithPath("data.writerInfo.nickname").description("빵 이야기 작성자 닉네임"),
					fieldWithPath("data.writerInfo.profileImage").description("빵 이야기 작성자 프로필 이미지"),
					fieldWithPath("data.writerInfo.reviewCount").description("빵 이야기 작성자 리뷰 개수"),
					fieldWithPath("data.writerInfo.followerCount").description("빵 이야기 작성자 팔로워 숫자"),
					fieldWithPath("data.writerInfo.isFollowed").description("빵 이야기 작성자 팔로우 여부"),
					fieldWithPath("data.images").description("빵 이야기 첨부 이미지"),
					fieldWithPath("data.content").description("빵 이야기 내용"),
					fieldWithPath("data.likeCount").description("빵 이야기 좋아요 개수"),
					fieldWithPath("data.commentCount").description("빵 이야기 댓글 개수"),
					fieldWithPath("data.createdDate").description("빵 이야기 작성 일자")
				)
			));
	}

}
