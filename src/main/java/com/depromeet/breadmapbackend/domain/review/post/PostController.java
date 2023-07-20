package com.depromeet.breadmapbackend.domain.review.post;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.depromeet.breadmapbackend.domain.review.post.dto.request.PostRequest;
import com.depromeet.breadmapbackend.domain.review.post.dto.response.CommunityCardResponse;
import com.depromeet.breadmapbackend.domain.review.post.dto.response.PostResponse;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;
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
	public void register(
		@AuthenticationPrincipal CurrentUserInfo currentUserInfo,
		@Valid final PostRequest request
	) {
		postService.register(Mapper.of(request), currentUserInfo.getId());
	}

	// post 상세 조회
	@GetMapping("/{postId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<PostResponse> getPost(
		@AuthenticationPrincipal CurrentUserInfo currentUserInfo,
		@PathVariable Long postId
	) {
		return new ApiResponse<>(postService.getPost(currentUserInfo.getId(), postId));
	}

	// post 전체 조회
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<PageResponseDto<CommunityCardResponse>> getPosts() {
		return new ApiResponse<>(postService.getPosts());
	}

	// post 추천글 조회
	@GetMapping("/hot")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<?> getHotPosts() {
		return new ApiResponse<>(postService.getHotPosts());
	}

	// post 삭제
	@DeleteMapping("/{postId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remove(
		@AuthenticationPrincipal CurrentUserInfo currentUserInfo,
		@PathVariable Long postId
	) {
		postService.remove(currentUserInfo.getId(), postId);
	}
}
