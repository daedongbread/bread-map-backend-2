package com.depromeet.breadmapbackend.domain.post.comment;

import java.sql.ResultSet;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.depromeet.breadmapbackend.domain.post.comment.dto.CommentQuery;

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

	private final NamedParameterJdbcTemplate jdbcTemplate;
	private static final Integer PAGE_SIZE = 10;
	private static final RowMapper<CommentQuery> COMMENT_QUERY_ROW_MAPPER = (ResultSet resultSet, int rowNum)
		-> new CommentQuery(
		resultSet.getLong("comment_id"),
		resultSet.getString("content"),
		resultSet.getBoolean("is_first_depth"),
		resultSet.getLong("parent_id"),
		resultSet.getLong("user_id"),
		resultSet.getString("nick_name"),
		resultSet.getString("image"),
		resultSet.getLong("like_count"),
		resultSet.getObject("createdDate", LocalDate.class),
		CommentStatus.valueOf(resultSet.getString("status")),
		resultSet.getBoolean("isBlocked")
	);

	public Page<CommentQuery> findComment(final Long postId, final Long userId, final int page) {
		final MapSqlParameterSource params = new MapSqlParameterSource()
			.addValue("limit", PAGE_SIZE)
			.addValue("offset", PAGE_SIZE * page)
			.addValue("postId", postId)
			.addValue("userId", userId);

		return new PageImpl<>(
			jdbcTemplate.query(getSortedCommentBaseSql(), params, COMMENT_QUERY_ROW_MAPPER),
			PageRequest.of(page, PAGE_SIZE),
			getAllCommentCount(postId)
		);
	}

	private String getSortedCommentBaseSql() {
		return """
			with recursive recursive_comments as (
				select id
					 , created_at
					 , user_id
					 , content
					 , post_id
					 , parent_id
					 , status
					 , is_first_depth
					 , 1 as depth
					 , created_at as sort_key
				from
					comment
				where
					parent_id = 0
				and post_id = :postId
					
				union all
					
				select c.id
					 , c.created_at
					 , c.user_id
					 , c.content
					 , c.post_id
					 , c.parent_id
					 , c.status
					 , c.is_first_depth
					 , rc.depth + 1 as depth
					 , rc.sort_key
				from
					comment c
					join recursive_comments rc on c.parent_id = rc.id
			)
			select rc.comment_id 
			     , rc.content
			     , rc.is_first_depth
			     , rc.parent_id
			     , rc.user_id
			     , u.nick_name
			     , u.image
			     , (select count(id)
			     	from comment_like cl
			     	where cl.comment_id = rc.commentId
			     	  and cl.status = 'ACTIVE') as like_count
			     , rc.createdDate
			     , rc.status
			     , case when bu.id is null then false 
			     		else true 
			     	end as isBlocked 
			from recursive_comments rc
			join user u on rc.user_id = u.id
			left join block_user bu on u.user_id = bu.to_user_id
									and bu.from_user_id = :userId
						
			order by
				sort_key,
				depth,
				created_at
			limit :limit offset :offset; 	
			""";
	}

	private Long getAllCommentCount(final Long postId) {
		final String sql = """
			 select count(*) 
			    from comment 			
			    where postId = :postId
			""";
		final MapSqlParameterSource param = new MapSqlParameterSource()
			.addValue("postId", postId);
		return jdbcTemplate.queryForObject(sql, param, Long.class);
	}

	// public Page<CommentQuery> findComment(final Long userId, final int page) {
	// 	final List<CommentQuery> results = queryFactory.select(Projections.constructor(CommentQuery.class
	// 			, comment.id
	// 			, new CaseBuilder()
	// 				.when(blockUser.isNull()).then(comment.content)
	// 				.otherwise("차단된 사용자의 댓글입니다.")
	// 			, comment.isFirstDepth
	// 			, comment.parentId
	// 			, comment.user.id
	// 			, new CaseBuilder()
	// 				.when(blockUser.isNull()).then(comment.user.userInfo.nickName)
	// 				.otherwise("차단된 사용자")
	// 			, new CaseBuilder()
	// 				.when(blockUser.isNull()).then(comment.user.userInfo.image)
	// 				.otherwise("차단된 사용자 이미지")
	// 			, JPAExpressions.select(commentLike.count().coalesce(0L))
	// 				.from(commentLike)
	// 				.where(commentLike.comment.id.eq(comment.id))
	// 			, comment.createdAt
	// 		))
	// 		.from(comment)
	// 		.join(comment.user, user).fetchJoin()
	// 		.join(comment.post, post).fetchJoin()
	// 		.leftJoin(blockUser).on(user.id.eq(blockUser.toUser.id)
	// 			.and(blockUser.fromUser.id.eq(userId))).fetchJoin()
	// 		.offset((long)page * PAGE_SIZE)
	// 		.limit(PAGE_SIZE)
	// 		.orderBy()
	// 		.fetch();
	//
	// 	JPAQuery<Long> countQuery = queryFactory
	// 		.select(comment.id)
	// 		.from(comment);
	//
	// 	return PageableExecutionUtils.getPage(
	// 		results,
	// 		PageRequest.of(page, PAGE_SIZE),
	// 		() -> countQuery.fetch().size()
	// 	);
	//
	// }
}
