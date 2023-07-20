package com.depromeet.breadmapbackend.domain.review.post;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

/**
 * PostQueryRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/20
 */
@Repository
@RequiredArgsConstructor
public class PostQueryRepository {
	private final JPAQueryFactory queryFactory;

	public Optional<Post> findPostDetailById(final Long postId, final Long userId) {
		return null;
	}
}
