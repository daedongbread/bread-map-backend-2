package com.depromeet.breadmapbackend.domain.post;

import static com.depromeet.breadmapbackend.domain.post.QPost.*;
import static com.depromeet.breadmapbackend.domain.post.comment.QComment.*;
import static com.depromeet.breadmapbackend.domain.post.like.QPostLike.*;
import static com.depromeet.breadmapbackend.domain.review.QReview.*;
import static com.depromeet.breadmapbackend.domain.user.QUser.*;
import static com.depromeet.breadmapbackend.domain.user.follow.QFollow.*;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.depromeet.breadmapbackend.domain.post.dto.CommunityCardInfo;
import com.depromeet.breadmapbackend.domain.post.dto.PostDetailInfo;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
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
	private final NamedParameterJdbcTemplate jdbcTemplate;
	private static final Integer PAGE_SIZE = 10;

	private static final RowMapper<CommunityCardInfo> COMMUNITY_CARD_INFO_ROW_MAPPER = (ResultSet resultSet, int rowNum)
		-> new CommunityCardInfo(
		resultSet.getLong("userId"),
		resultSet.getString("nickname"),
		resultSet.getString("profileImage"),
		resultSet.getLong("postId"),
		resultSet.getString("title"),
		resultSet.getString("content"),
		resultSet.getString("thumbnail"),
		resultSet.getObject("createdDate", LocalDateTime.class),
		PostTopic.valueOf(resultSet.getString("topic")),
		resultSet.getLong("likeCount"),
		resultSet.getLong("commentCount"),
		resultSet.getLong("bakeryId"),
		resultSet.getString("name"),
		resultSet.getString("address"),
		resultSet.getString("bakeryThumbnail")
	);

	public Optional<PostDetailInfo> findPostDetailById(final Long postId, final Long userId) {
		return Optional.ofNullable(
			queryFactory.
				select(Projections.constructor(PostDetailInfo.class,
					post,
					JPAExpressions.select(postLike.count().coalesce(0L))
						.from(postLike)
						.where(postLike.post.id.eq(post.id)),
					JPAExpressions.select(comment.count().coalesce(0L))
						.from(comment)
						.where(comment.post.id.eq(post.id)),
					JPAExpressions.select(review.count().coalesce(0L))
						.from(review)
						.where(review.user.id.eq(post.user.id)),
					JPAExpressions.select(follow.count().coalesce(0L))
						.from(follow)
						.where(follow.toUser.id.eq(post.user.id)),
					new CaseBuilder().when(JPAExpressions.select(follow.count())
							.from(follow)
							.where(follow.fromUser.id.eq(userId)
								.and(follow.toUser.id.eq(post.user.id))).eq(1L)).then(true)
						.otherwise(false)

				))
				.from(post)
				.join(post.user, user).fetchJoin()
				.where(post.id.eq(postId))
				.fetchOne()
		);
	}

	public Page<CommunityCardInfo> findAllCommunityCards(final CommunityPage communityPage) {

		final MapSqlParameterSource params = new MapSqlParameterSource()
			.addValue("limit", PAGE_SIZE)
			.addValue("reviewOffset", communityPage.reviewOffset())
			.addValue("postOffset", communityPage.postOffset());

		final String postBaseSql = getPostBaseSqlWithWhereClaus("1 = 1");
		final String reviewBaseSql = getReviewBaseSql();
		final String sql = String.format("""
				with post_base as(
					%s
				),
				review_base as(
					%s
				)
			 	select community.*
			 	from (select * from post_base union all select * from review_base ) community
				order by community.sortOrder asc, community.createdDate desc, community.postId desc
			""", postBaseSql, reviewBaseSql);
		return new PageImpl<>(
			jdbcTemplate.query(sql, params, COMMUNITY_CARD_INFO_ROW_MAPPER),
			PageRequest.of(communityPage.page(), PAGE_SIZE),
			20L
		);
	}

	public Page<CommunityCardInfo> findBreadStoryCards(final CommunityPage communityPage) {

		final MapSqlParameterSource params = new MapSqlParameterSource()
			.addValue("limit", PAGE_SIZE)
			.addValue("postOffset", communityPage.postOffset());

		final String postBaseSql = getPostBaseSqlWithWhereClaus(
			"(t4.is_fixed is true or t1.post_topic ='BREAD_STORY')");
		List<CommunityCardInfo> cards = jdbcTemplate.query(postBaseSql, params, COMMUNITY_CARD_INFO_ROW_MAPPER);
		return new PageImpl<>(cards, PageRequest.of(communityPage.page(), PAGE_SIZE), 20L);
	}

	public Page<CommunityCardInfo> findEventCards(final CommunityPage communityPage) {

		final MapSqlParameterSource params = new MapSqlParameterSource()
			.addValue("limit", PAGE_SIZE)
			.addValue("postOffset", communityPage.postOffset());

		final String postBaseSql = getPostBaseSqlWithWhereClaus(
			"(t1.post_topic = 'EVENT')");
		List<CommunityCardInfo> cards = jdbcTemplate.query(postBaseSql, params, COMMUNITY_CARD_INFO_ROW_MAPPER);
		return new PageImpl<>(cards, PageRequest.of(communityPage.page(), PAGE_SIZE), 20L);
	}

	private String getPostBaseSqlWithWhereClaus(final String whereClause) {
		return String.format("""
			select t2.id                          as userId
				 , t2.nick_name                   as nickname
				 , t2.image                       as profileImage
				 , t1.id                          as postId
				 , t1.title                       as title
				 , t1.content                     as content
				 , (select image
					 from post_image
				   	where post_id = t1.id
				   	order by created_at
				   	limit 1)                      as thumbnail
				 , t1.created_at                  as createdDate
				 , t1.post_topic                  as topic
				 , (select count(pl.id)
				   	  from post_like pl 
				   	  where pl.post_id = t1.id)   as likeCount
				 , (select count(cmmt.id) 
				      from comment cmmt 
				      where cmmt.post_id = t1.id) as commentCount
				 , null                           as bakeryId
				 , null                           as name
				 , null                           as address
				 , null                           as bakeryThumbnail
				 , case when t4.is_fixed is true 
				 			then 1 
						else 2 
					end 						  as sortOrder
			   from post t1
			   inner join user t2 on t1.user_id = t2.id
			   left join post_manager_mapper t4 on t1.id = t4.post_id 
			   									and t4.is_fixed is true
			   where %s
			   order by t4.is_fixed desc, t1.created_at desc, t1.id desc
			   limit :limit offset :postOffset 
			""", whereClause);
	}

	private String getReviewBaseSql() {
		return """
				select t2.id                                   as userId
				     , t2.nick_name                            as nickname
				     , t2.image                                as profileImage
				     , t1.id                                   as postId
				     , null                                    as title
				     , t1.content                              as content
				     , (select image
						from review_image
						where review_id = t1.id
						order by created_at
						limit 1)                               as thumbnail
				     , t1.created_at                           as createdDate
				     , 'REVIEW'                                as topic
				     , (select count(rl.id)
						  from review_like rl
						  where rl.review_id = t1.id)        as likeCount
				     , (select count(cmmt.id) 
						  from review_comment cmmt
						  where cmmt.review_id = t1.id) as commentCount
				     , t3.id                                    as bakeryId
				     , t3.name                                  as name
				     , t3.address                               as address
				     , t3.image                                 as bakeryThumbnail
				     , 2 										as sortOrder
				from review t1
			    inner join user t2 on t1.user_id = t2.id
			    inner join bakery t3 on t1.bakery_id = t3.id
			 
				order by t1.created_at desc , t1.id desc
				limit :limit offset :reviewOffset		
			""";
	}

	public Page<CommunityCardInfo> findReviewCards(final CommunityPage communityPage) {
		throw new IllegalStateException("PostQueryRepository::findReviewCards not implemented yet");
	}
}
