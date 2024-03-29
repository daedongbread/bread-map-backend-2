package com.depromeet.breadmapbackend.domain.post;

import java.util.List;
import java.util.Optional;

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
	public Page<CommunityCardInfo> findAllCommunityCards(final CommunityPage communityPage, final Long userId) {
		return postQueryRepository.findAllCommunityCards(communityPage, userId);
	}

	@Override
	public Page<CommunityCardInfo> findUserBoardCards(
		final CommunityPage communityPage,
		final Long userId
	) {
		return postQueryRepository.findUserBoardCards(communityPage, userId);
	}

	@Override
	public Page<CommunityCardInfo> findEventCards(final CommunityPage communityPage, final Long userId) {
		return postQueryRepository.findEventCards(communityPage, userId);
	}

	@Override
	public Page<CommunityCardInfo> findReviewCards(final CommunityPage communityPage, final Long userId) {
		return postQueryRepository.findReviewCards(communityPage, userId);
	}

	@Override
	public PostDetailInfo findPostDetailBy(
		final Long postId,
		final Long userId,
		final PostTopic postTopic
	) {
		return postQueryRepository.findPostDetailById(postId, userId, postTopic)
			.orElseThrow(() -> new DaedongException(DaedongStatus.POST_NOT_FOUND))
			.toInfo();
	}

	@Override
	public void delete(final Post post) {
		postJpaRepository.delete(post);
	}

	@Override
	public List<CommunityCardInfo> findHotPosts(final Long userId) {
		return postQueryRepository.findHotPosts(userId);
	}

	@Override
	public Optional<Post> findByPostIdAndUserIdAndPostTopic(
		final Long postId,
		final Long userId,
		final PostTopic postTopic
	) {
		return postJpaRepository.findByIdAndUserIdAndPostTopic(postId, userId, postTopic);
	}

	@Override
	public Optional<Post> findByPostIdAndPostTopic(final Long postId, final PostTopic postTopic) {
		return postJpaRepository.findByIdAndPostTopic(postId, postTopic);
	}

	@Override
	public Optional<Post> findByPostIdAndPostTopic(final Long postId, final String postTopic) {
		return postJpaRepository.findByIdAndPostTopic(postId, PostTopic.of(postTopic));
	}

	@Override
	public Optional<Post> findById(final Long postId) {
		return postJpaRepository.findById(postId);
	}
}
