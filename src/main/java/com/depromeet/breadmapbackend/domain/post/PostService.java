package com.depromeet.breadmapbackend.domain.post;

import java.util.List;

import com.depromeet.breadmapbackend.domain.post.dto.PostDetailInfo;
import com.depromeet.breadmapbackend.domain.post.dto.PostRegisterCommand;
import com.depromeet.breadmapbackend.domain.post.dto.PostUpdateCommand;
import com.depromeet.breadmapbackend.domain.post.dto.response.CommunityCardResponse;
import com.depromeet.breadmapbackend.global.dto.PageCommunityResponseDto;

public interface PostService {

	// post 등록
	void register(
		final PostRegisterCommand command,
		final Long userId
	);

	PageCommunityResponseDto<CommunityCardResponse> getCommunityCards(
		final CommunityPage page,
		final Long userId
	);

	// post 상세 조회
	PostDetailInfo getDetailPost(
		final Long userId,
		final Long postId,
		final PostTopic postTopic
	);

	// post 추천글 조회
	List<CommunityCardResponse> findHotPosts(
		final Long userId
	);

	// post 삭제
	void delete(
		final Long postId,
		final PostTopic topic,
		final Long userId
	);

	// post 수정
	void update(
		final Long userId,
		final PostUpdateCommand command
	);

	// post 좋아요
	int toggle(
		final Long postId,
		final Long userId
	);

}

