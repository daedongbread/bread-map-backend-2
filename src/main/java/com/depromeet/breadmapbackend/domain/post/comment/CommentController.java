package com.depromeet.breadmapbackend.domain.post.comment;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.depromeet.breadmapbackend.domain.post.comment.dto.request.CommentCreateRequest;
import com.depromeet.breadmapbackend.domain.post.comment.dto.request.CommentUpdateRequest;
import com.depromeet.breadmapbackend.domain.post.comment.dto.response.CommentResponse;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;
import com.depromeet.breadmapbackend.global.security.userinfo.CurrentUserInfo;

import lombok.RequiredArgsConstructor;

/**
 * CommentController
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/21
 */

@RestController
@RequestMapping("/v1/comments")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	//댓글 등록
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	void register(
		@AuthenticationPrincipal final CurrentUserInfo currentUserInfo,
		@RequestBody @Valid final CommentCreateRequest request
	) {
		commentService.register(Mapper.of(request), currentUserInfo.getId());
	}

	// 댓글 조회
	@GetMapping("/{postId}/{page}")
	@ResponseStatus(HttpStatus.OK)
	ApiResponse<PageResponseDto<CommentResponse>> findComment(
		@AuthenticationPrincipal final CurrentUserInfo currentUserInfo,
		@PathVariable("page") final int page,
		@PathVariable("postId") final Long postId
	) {
		return new ApiResponse<>(Mapper.of(commentService.findComment(postId, currentUserInfo.getId(), page)));
	}

	//댓글 수정
	@PutMapping
	@ResponseStatus(HttpStatus.OK)
	void updateComment(
		@AuthenticationPrincipal final CurrentUserInfo currentUserInfo,
		@RequestBody @Valid final CommentUpdateRequest request
	) {
		commentService.updateComment(Mapper.of(request), currentUserInfo.getId());
	}

	//댓글 삭제
	@DeleteMapping("/{commentId}")
	@ResponseStatus(HttpStatus.OK)
	void deleteComment(
		@AuthenticationPrincipal final CurrentUserInfo currentUserInfo,
		@PathVariable final Long commentId
	) {
		commentService.deleteComment(commentId, currentUserInfo.getId());
	}

	//댓글 좋아요

	//댓글 신고
}
