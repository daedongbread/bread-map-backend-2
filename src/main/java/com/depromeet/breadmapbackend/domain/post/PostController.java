package com.depromeet.breadmapbackend.domain.post;

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

import com.depromeet.breadmapbackend.domain.post.dto.PostReportRequest;
import com.depromeet.breadmapbackend.domain.post.dto.request.PostRequest;
import com.depromeet.breadmapbackend.domain.post.dto.response.CommunityCardResponse;
import com.depromeet.breadmapbackend.domain.post.dto.response.PostResponse;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;
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
		@AuthenticationPrincipal CurrentUserInfo currentUserInfo,
		@Valid final PostRequest request
	) {
		postService.register(Mapper.of(request), currentUserInfo.getId());
	}

	// post 상세 조회
	@GetMapping("/{postId}")
	@ResponseStatus(HttpStatus.OK)
	ApiResponse<PostResponse> getPost(
		@AuthenticationPrincipal CurrentUserInfo currentUserInfo,
		@PathVariable Long postId
	) {
		return new ApiResponse<>(
			Mapper.of(postService.getPost(currentUserInfo.getId(), postId))
		);
	}

	// post 전체 조회
	@GetMapping("/all")
	@ResponseStatus(HttpStatus.OK)
	ApiResponse<PageResponseDto<CommunityCardResponse>> getAllPosts(
		@AuthenticationPrincipal CurrentUserInfo currentUserInfo,
		@Valid final CommunityPage page
	) {
		return new ApiResponse<>(postService.getAllPosts(page, currentUserInfo.getId()));
	}

	// post 빵이야기 조회
	@GetMapping("/bread-story")
	@ResponseStatus(HttpStatus.OK)
	ApiResponse<PageResponseDto<CommunityCardResponse>> getBreadStoryPosts(
		@AuthenticationPrincipal CurrentUserInfo currentUserInfo,
		@Valid final CommunityPage page
	) {
		return new ApiResponse<>(postService.getBreadStoryPosts(page, currentUserInfo.getId()));
	}

	// post 리뷰 조회
	@GetMapping("/review")
	@ResponseStatus(HttpStatus.OK)
	ApiResponse<PageResponseDto<CommunityCardResponse>> getReviewPosts(
		@AuthenticationPrincipal CurrentUserInfo currentUserInfo,
		@Valid final CommunityPage page
	) {
		return new ApiResponse<>(postService.getAllPosts(page, currentUserInfo.getId()));
	}

	// post 이벤트 조회
	@GetMapping("/event")
	@ResponseStatus(HttpStatus.OK)
	ApiResponse<PageResponseDto<CommunityCardResponse>> getEventPosts(
		@AuthenticationPrincipal CurrentUserInfo currentUserInfo,
		@Valid final CommunityPage page
	) {
		return new ApiResponse<>(postService.getAllPosts(page, currentUserInfo.getId()));
	}

	// post 추천글 조회
	@GetMapping("/hot")
	@ResponseStatus(HttpStatus.OK)
	ApiResponse<?> getHotPosts(
		@AuthenticationPrincipal CurrentUserInfo currentUserInfo
	) {
		return new ApiResponse<>(postService.getHotPosts(currentUserInfo.getId()));
	}

	// post 삭제
	@DeleteMapping("/{postId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void remove(
		@AuthenticationPrincipal CurrentUserInfo currentUserInfo,
		@PathVariable Long postId
	) {
		postService.remove(currentUserInfo.getId(), postId);
	}

	// post 수정
	@PutMapping("/{postId}")
	@ResponseStatus(HttpStatus.CREATED)
	void update(
		@PathVariable("postId") final Long postId,
		@AuthenticationPrincipal CurrentUserInfo currentUserInfo,
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
