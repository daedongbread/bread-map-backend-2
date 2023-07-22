package com.depromeet.breadmapbackend.domain.post;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.depromeet.breadmapbackend.domain.post.dto.request.PostReportRequest;
import com.depromeet.breadmapbackend.domain.post.dto.request.PostRequest;
import com.depromeet.breadmapbackend.domain.post.dto.response.CommunityCardResponse;
import com.depromeet.breadmapbackend.domain.post.dto.response.PostResponse;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.dto.PageCommunityResponseDto;
import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import com.depromeet.breadmapbackend.global.security.userinfo.CurrentUserInfo;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/posts")
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	// post 등록
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	void register(
		@AuthenticationPrincipal final CurrentUserInfo currentUserInfo,
		@RequestBody @Valid final PostRequest request
	) {
		postService.register(Mapper.of(request), currentUserInfo.getId());
	}

	// post 상세 조회
	@GetMapping("/{postId}/{postTopic}")
	@ResponseStatus(HttpStatus.OK)
	ApiResponse<PostResponse> getPost(
		@AuthenticationPrincipal final CurrentUserInfo currentUserInfo,
		@PathVariable("postId") final Long postId,
		@PathVariable("postTopic") final String postTopic
	) {
		final PostTopic topic = PostTopic.of(postTopic);
		return new ApiResponse<>(
			Mapper.of(postService.getPost(postId, currentUserInfo.getId(), topic))
		);
	}

	// post 전체 조회
	@GetMapping("/cards/{postTopic}")
	@ResponseStatus(HttpStatus.OK)
	ApiResponse<PageCommunityResponseDto<CommunityCardResponse>> getCommunityCards(
		@PathVariable("postTopic") final String postTopic,
		@AuthenticationPrincipal final CurrentUserInfo currentUserInfo,
		@Valid final CommunityPage page
	) {
		final PostTopic topic = PostTopic.of(postTopic);
		return new ApiResponse<>(postService.getCommunityCards(page, currentUserInfo.getId(), topic));
	}

	// post 추천글 조회
	@GetMapping("/hot")
	@ResponseStatus(HttpStatus.OK)
	ApiResponse<List<CommunityCardResponse>> findHotPosts(
		@AuthenticationPrincipal final CurrentUserInfo currentUserInfo
	) {
		return new ApiResponse<>(postService.findHotPosts(currentUserInfo.getId()));
	}

	// post 삭제
	@DeleteMapping("/{postId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void remove(
		@AuthenticationPrincipal final CurrentUserInfo currentUserInfo,
		@PathVariable final Long postId
	) {
		postService.remove(currentUserInfo.getId(), postId);
	}

	// post 수정
	@PutMapping("/{postId}")
	@ResponseStatus(HttpStatus.CREATED)
	void update(
		@PathVariable("postId") final Long postId,
		@AuthenticationPrincipal final CurrentUserInfo currentUserInfo,
		@Valid final PostRequest request
	) {
		postService.update(currentUserInfo.getId(), Mapper.of(request, postId));
	}

	// post 좋아요 토글
	@PostMapping("/like/{postId}")
	@ResponseStatus(HttpStatus.OK)
	void toggleLike(
		@PathVariable("postId") final Long postId,
		@AuthenticationPrincipal final CurrentUserInfo currentUserInfo
	) {
		postService.toggleLike(currentUserInfo.getId(), postId);
	}

	// post 신고
	@PostMapping("/report/{postId}")
	@ResponseStatus(HttpStatus.CREATED)
	void report(
		@PathVariable("postId") final Long postId,
		@RequestBody @Validated(ValidationSequence.class) final PostReportRequest request,
		@AuthenticationPrincipal final CurrentUserInfo currentUserInfo
	) {
		postService.report(currentUserInfo.getId(), Mapper.of(postId, request));
	}
}
