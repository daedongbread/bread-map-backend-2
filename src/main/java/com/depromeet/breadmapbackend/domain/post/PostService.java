package com.depromeet.breadmapbackend.domain.post;

import java.util.List;

import com.depromeet.breadmapbackend.domain.post.dto.PostDetailInfo;
import com.depromeet.breadmapbackend.domain.post.dto.PostRegisterCommand;
import com.depromeet.breadmapbackend.domain.post.dto.response.CommunityCardResponse;
import com.depromeet.breadmapbackend.domain.post.dto.response.PostResponse;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;

public interface PostService {

	// post 등록
	void register(
		final PostRegisterCommand command,
		final Long userId
	);

	PageResponseDto<CommunityCardResponse> getAllPosts(
		final CommunityPage page,
		final Long userId

	);

	PageResponseDto<CommunityCardResponse> getBreadStoryPosts(
		final CommunityPage page,
		final Long userId
	);

	PageResponseDto<CommunityCardResponse> getReviewPosts(
		final CommunityPage page,
		final Long userId
	);

	PageResponseDto<CommunityCardResponse> getEventPosts(
		final CommunityPage page,
		final Long userId
	);

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
