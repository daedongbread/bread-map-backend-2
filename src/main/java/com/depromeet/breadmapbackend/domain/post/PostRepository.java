package com.depromeet.breadmapbackend.domain.post;

import org.springframework.data.domain.Page;

import com.depromeet.breadmapbackend.domain.post.dto.CommunityCardInfo;
import com.depromeet.breadmapbackend.domain.post.dto.PostDetailInfo;

/**
 * PostRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/20
 */
public interface PostRepository {
	Post save(Post entity);

	Page<CommunityCardInfo> findAllCommunityCards(final CommunityPage communityPage, final Long userId);

	Page<CommunityCardInfo> findBreadStoryCards(final CommunityPage communityPage, final Long userId);

	Page<CommunityCardInfo> findEventCards(final CommunityPage communityPage, final Long userId);

	Page<CommunityCardInfo> findReviewCards(final CommunityPage communityPage, final Long userId);

	PostDetailInfo findPostBy(final Long postId, final Long userId, final PostTopic postTopic);

	void deletePostById(Long postId, Long userId);
}
