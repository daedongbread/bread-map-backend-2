package com.depromeet.breadmapbackend.domain.post;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.depromeet.breadmapbackend.domain.post.dto.request.PostRequest;
import com.depromeet.breadmapbackend.domain.post.dto.response.CommunityCardResponse;
import com.depromeet.breadmapbackend.domain.post.dto.response.PostResponse;
import com.depromeet.breadmapbackend.global.dto.ApiResponse;
import com.depromeet.breadmapbackend.global.dto.PageCommunityResponseDto;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
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
		validatePostTopic(request.postTopic());
		postService.register(Mapper.of(request), currentUserInfo.getId());
	}

	// post 상세 조회
	@GetMapping("/{postTopic}/{postId}")
	@ResponseStatus(HttpStatus.OK)
	ApiResponse<PostResponse> getPost(
		@AuthenticationPrincipal final CurrentUserInfo currentUserInfo,
		@PathVariable("postId") final Long postId,
		@PathVariable("postTopic") final String postTopic
	) {
		final PostTopic topic = PostTopic.of(postTopic);
		return new ApiResponse<>(
			Mapper.of(postService.getDetailPost(postId, currentUserInfo.getId(), topic))
		);
	}

	// post 전체 조회
	@GetMapping("/cards/{postTopic}")
	@ResponseStatus(HttpStatus.OK)
	ApiResponse<PageCommunityResponseDto<CommunityCardResponse>> getCommunityCards(
		@PathVariable("postTopic") final String postTopic,
		@RequestParam(value = "reviewOffset", required = false, defaultValue = "0") long reviewOffset,
		@RequestParam(value = "postOffset", required = false, defaultValue = "0") long postOffset,
		@RequestParam(value = "page", required = false, defaultValue = "0") int page,
		@AuthenticationPrincipal final CurrentUserInfo currentUserInfo
	) {
		final PostTopic topic = PostTopic.of(postTopic);
		return new ApiResponse<>(
			postService.getCommunityCards(new CommunityPage(reviewOffset, postOffset, topic, page),
				currentUserInfo.getId()));
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
	@DeleteMapping("/{postTopic}/{postId}")
	@ResponseStatus(HttpStatus.OK)
	void remove(
		@PathVariable("postId") final Long postId,
		@PathVariable("postTopic") final String postTopic,
		@AuthenticationPrincipal final CurrentUserInfo currentUserInfo
	) {
		final PostTopic topic = PostTopic.of(postTopic);
		validatePostTopic(topic);
		postService.delete(postId, topic, currentUserInfo.getId());
	}

	// post 수정
	@PutMapping("/{postId}")
	@ResponseStatus(HttpStatus.OK)
	void update(
		@PathVariable("postId") final Long postId,
		@AuthenticationPrincipal final CurrentUserInfo currentUserInfo,
		@RequestBody @Valid final PostRequest request
	) {
		validatePostTopic(request.postTopic());
		postService.update(currentUserInfo.getId(), Mapper.of(request, postId));
	}

	// post 좋아요 토글
	@PostMapping("/like/{postId}")
	@ResponseStatus(HttpStatus.OK)
	ApiResponse<Integer> toggle(
		@PathVariable("postId") final Long postId,
		@AuthenticationPrincipal final CurrentUserInfo currentUserInfo
	) {
		return new ApiResponse<>(postService.toggle(postId, currentUserInfo.getId()));
	}

	private void validatePostTopic(final PostTopic postTopic) {
		if (postTopic.equals(PostTopic.EVENT) ||
			postTopic.equals(PostTopic.REVIEW)
		) {
			throw new DaedongException(DaedongStatus.INVALID_POST_TOPIC);
		}
	}

}
