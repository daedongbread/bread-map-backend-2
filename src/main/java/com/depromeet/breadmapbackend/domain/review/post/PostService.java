package com.depromeet.breadmapbackend.domain.review.post;

import java.util.List;

import com.depromeet.breadmapbackend.domain.review.post.dto.PostRequestDto;
import com.depromeet.breadmapbackend.domain.review.post.dto.PostResponseDto;

public interface PostService {

	// post 등록
	void register(
		PostRequestDto postRequestDto,
		String oAuthId
	);

	// post 조회
	List<PostResponseDto> getPosts();

	// post 상세 조회
	PostResponseDto getPost(Long postId);

	// post 추천글 조회
	List<PostResponseDto> getHotPosts();

	// post 삭제
	void remove(
		String oAuthId,
		Long postId
	);
}
