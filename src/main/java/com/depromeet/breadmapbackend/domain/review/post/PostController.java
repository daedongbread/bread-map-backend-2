package com.depromeet.breadmapbackend.domain.review.post;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.depromeet.breadmapbackend.domain.review.dto.ReviewRequest;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.exception.ValidationSequence;
import com.depromeet.breadmapbackend.global.security.CurrentUser;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/posts")
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	// post 등록
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void register(@CurrentUser String oAuthId) {
		postService.register(oAuthId);
	}

	// post 상세 조회
	@GetMapping("/{postId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<?> getPost(@PathVariable Long postId) {
		return new ApiResponse<>(postService.getPost(postId));
	}

	// post 전체 조회
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<?> getPosts() {
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
		@CurrentUser String oAuthId,
		@PathVariable Long postId
	) {
		postService.remove(oAuthId, postId);
	}
}
