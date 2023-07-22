package com.depromeet.breadmapbackend.domain.post.comment;

import static com.depromeet.breadmapbackend.domain.post.QPost.*;
import static com.depromeet.breadmapbackend.domain.post.comment.QComment.*;
import static com.depromeet.breadmapbackend.domain.post.comment.like.QCommentLike.*;
import static com.depromeet.breadmapbackend.domain.user.QUser.*;
import static com.depromeet.breadmapbackend.domain.user.block.QBlockUser.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.depromeet.breadmapbackend.domain.post.comment.dto.CommentQuery;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

/**
 * CommentQueryRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/23
 */
@Repository
@RequiredArgsConstructor
public class CommentQueryRepository {

	private final JPAQueryFactory queryFactory;
	private static final Integer PAGE_SIZE = 10;

	public Page<CommentQuery> findComment(final Long userId, final int page) {
		final List<CommentQuery> results = queryFactory.select(Projections.constructor(CommentQuery.class
				, comment.id
				, new CaseBuilder()
					.when(blockUser.isNull()).then(comment.content)
					.otherwise("차단된 사용자의 댓글입니다.")
				, comment.isFirstDepth
				, comment.parentId
				, comment.user.id
				, new CaseBuilder()
					.when(blockUser.isNull()).then(comment.user.userInfo.nickName)
					.otherwise("차단된 사용자")
				, new CaseBuilder()
					.when(blockUser.isNull()).then(comment.user.userInfo.image)
					.otherwise("차단된 사용자 이미지")
				, JPAExpressions.select(commentLike.count().coalesce(0L))
					.from(commentLike)
					.where(commentLike.comment.id.eq(comment.id))
				, comment.createdAt
			))
			.from(comment)
			.join(comment.user, user).fetchJoin()
			.join(comment.post, post).fetchJoin()
			.leftJoin(blockUser).on(user.id.eq(blockUser.toUser.id)
				.and(blockUser.fromUser.id.eq(userId))).fetchJoin()
			.offset((long)page * PAGE_SIZE)
			.limit(PAGE_SIZE)
			.orderBy()
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(comment.id)
			.from(comment);

		return PageableExecutionUtils.getPage(
			results,
			PageRequest.of(page, PAGE_SIZE),
			() -> countQuery.fetch().size()
		);

	}
}
