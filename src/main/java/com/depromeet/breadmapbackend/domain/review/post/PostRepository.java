package com.depromeet.breadmapbackend.domain.review.post;

import com.depromeet.breadmapbackend.domain.review.post.dto.PostDetailInfo;
import com.depromeet.breadmapbackend.domain.review.post.dto.response.CommunityCardResponse;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;

/**
 * PostRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/20
 */
public interface PostRepository {
	Post save(Post entity);

	PageResponseDto<CommunityCardResponse> findAllCommunityCards();

	PostDetailInfo findPostBy(Long postId, Long userId);

	void deletePostById(Long postId, Long userId);
}
