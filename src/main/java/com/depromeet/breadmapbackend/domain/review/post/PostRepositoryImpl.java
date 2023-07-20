package com.depromeet.breadmapbackend.domain.review.post;

import org.springframework.stereotype.Repository;

import com.depromeet.breadmapbackend.domain.review.post.dto.response.CommunityCardResponse;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;
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
	public PageResponseDto<CommunityCardResponse> findAllCommunityCards() {
		return null;
	}

	@Override
	public Post findPostBy(final Long postId, final Long userId) {
		return postQueryRepository.findPostDetailById(postId, userId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.POST_NOT_FOUND));
	}

	@Override
	public void deletePostById(final Long postId, final Long userId) {

	}
}
