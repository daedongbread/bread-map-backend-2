package com.depromeet.breadmapbackend.domain.review.post;

import java.util.List;

import com.depromeet.breadmapbackend.domain.review.post.dto.PostDetailInfo;
import com.depromeet.breadmapbackend.domain.review.post.dto.PostRegisterCommand;
import com.depromeet.breadmapbackend.domain.review.post.dto.response.CommunityCardResponse;
import com.depromeet.breadmapbackend.domain.review.post.dto.response.PostResponse;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;

public interface PostService {

	// post 등록
	void register(
		final PostRegisterCommand command,
		final Long userId
	);

	// post 조회
	PageResponseDto<CommunityCardResponse> getPosts();

	// post 상세 조회
	PostDetailInfo getPost(
		final Long userId,
		final Long postId
	);

	// post 추천글 조회
	List<PostResponse> getHotPosts();

	// post 삭제
	void remove(
		Long userI,
		Long postId
	);
}
