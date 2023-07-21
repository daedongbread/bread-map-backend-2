package com.depromeet.breadmapbackend.domain.post;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import com.depromeet.breadmapbackend.domain.post.dto.CommunityCardInfo;
import com.depromeet.breadmapbackend.domain.post.dto.PostDetailInfo;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.RequiredArgsConstructor;

/**
 * PostRepositoryImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/20
 */
@RequiredArgsConstructor
@Repository
public class PostRepositoryImpl implements PostRepository {

	private final PostJpaRepository postJpaRepository;
	private final PostQueryRepository postQueryRepository;

	@Override
	public Post save(final Post entity) {
		return postJpaRepository.save(entity);
	}

	@Override
	public Page<CommunityCardInfo> findAllCommunityCards(final CommunityPage communityPage) {
		return postQueryRepository.findAllCommunityCards(communityPage);
	}

	@Override
	public Page<CommunityCardInfo> findBreadStoryCards(final CommunityPage communityPage) {
		return postQueryRepository.findBreadStoryCards(communityPage);
	}

	@Override
	public Page<CommunityCardInfo> findEventCards(final CommunityPage communityPage) {
		return postQueryRepository.findEventCards(communityPage);
	}

	@Override
	public Page<CommunityCardInfo> findReviewCards(final CommunityPage communityPage) {
		return postQueryRepository.findReviewCards(communityPage);
	}

	@Override
	public PostDetailInfo findPostBy(final Long postId, final Long userId) {
		return postQueryRepository.findPostDetailById(postId, userId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.POST_NOT_FOUND));
	}

	@Override
	public void deletePostById(final Long postId, final Long userId) {

	}
}
