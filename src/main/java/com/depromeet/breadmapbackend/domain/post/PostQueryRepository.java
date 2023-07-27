package com.depromeet.breadmapbackend.domain.post;

import static com.depromeet.breadmapbackend.domain.post.QPost.*;
import static com.depromeet.breadmapbackend.domain.post.comment.QComment.*;
import static com.depromeet.breadmapbackend.domain.post.like.QPostLike.*;
import static com.depromeet.breadmapbackend.domain.review.QReview.*;
import static com.depromeet.breadmapbackend.domain.review.comment.QReviewComment.*;
import static com.depromeet.breadmapbackend.domain.review.like.QReviewLike.*;
import static com.depromeet.breadmapbackend.domain.user.QUser.*;
import static com.depromeet.breadmapbackend.domain.user.block.QBlockUser.*;
import static com.depromeet.breadmapbackend.domain.user.follow.QFollow.*;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.depromeet.breadmapbackend.domain.post.dto.CommunityCardInfo;
import com.depromeet.breadmapbackend.domain.post.dto.PostDetailQuery;
import com.querydsl.core.Tuple;
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
		resultSet.getString("bakeryThumbnail"),
		resultSet.getBoolean("isUserLiked"),
		resultSet.getBoolean("isUserCommented")
	);

	public Optional<PostDetailQuery> findPostDetailById(final Long postId, final Long userId, final PostTopic topic) {
		return Optional.ofNullable(
			queryFactory.
				selectDistinct(Projections.constructor(PostDetailQuery.class,
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
						.otherwise(false),
					new CaseBuilder().when(JPAExpressions.select(postLike.count())
							.from(postLike)
							.where(postLike.postId.eq(post.id)
								.and(postLike.userId.eq(userId))).goe(1L)).then(true)
						.otherwise(false),
					new CaseBuilder().when(JPAExpressions.select(comment.count())
							.from(comment)
							.where(comment.postId.eq(post.id)
								.and(comment.user.id.eq(post.user.id))).goe(1L)).then(true)
						.otherwise(false)
				))
				.from(post)
				.join(post.user, user).fetchJoin()
				.where(post.id.eq(postId).and(post.postTopic.eq(topic)))
				.fetchOne()
		);
	}

	public Page<CommunityCardInfo> findAllCommunityCards(final CommunityPage communityPage, final Long userId) {

		final MapSqlParameterSource params = new MapSqlParameterSource()
			.addValue("limit", PAGE_SIZE)
			.addValue("reviewOffset", communityPage.reviewOffset())
			.addValue("postOffset", communityPage.postOffset())
			.addValue("userId", userId);

		final String postBaseSql = getPostBaseSqlWithWhereClaus("1 = 1");
		final String reviewBaseSql = getReviewBaseSql("");
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
			getAllCardsCount(userId)
		);
	}

	public Page<CommunityCardInfo> findBreadStoryCards(
		final CommunityPage communityPage,
		final Long userId,
		final PostTopic postTopic
	) {

		final MapSqlParameterSource params = new MapSqlParameterSource()
			.addValue("limit", PAGE_SIZE)
			.addValue("postOffset", communityPage.postOffset())
			.addValue("userId", userId);

		final String whereClaus = String.format("(t4.is_fixed is true or t1.post_topic = '%s')", postTopic.name());
		final List<CommunityCardInfo> cards =
			jdbcTemplate.query(
				getPostBaseSqlWithWhereClaus(whereClaus),
				params,
				COMMUNITY_CARD_INFO_ROW_MAPPER
			);

		return new PageImpl<>(
			cards,
			PageRequest.of(communityPage.page(), PAGE_SIZE),
			getPostsCardsCount(PostTopic.BREAD_STORY, 1, userId)
		);
	}

	public Page<CommunityCardInfo> findEventCards(final CommunityPage communityPage, final Long userId) {

		final MapSqlParameterSource params = new MapSqlParameterSource()
			.addValue("limit", PAGE_SIZE)
			.addValue("postOffset", communityPage.postOffset())
			.addValue("userId", userId);

		final List<CommunityCardInfo> cards =
			jdbcTemplate.query(
				getPostBaseSqlWithWhereClaus("(t1.post_topic = 'EVENT')"),
				params,
				COMMUNITY_CARD_INFO_ROW_MAPPER
			);

		return new PageImpl<>(
			cards,
			PageRequest.of(communityPage.page(), PAGE_SIZE),
			getPostsCardsCount(PostTopic.EVENT, 0, userId)
		);
	}

	public Page<CommunityCardInfo> findReviewCards(final CommunityPage communityPage, final Long userId) {

		final MapSqlParameterSource params = new MapSqlParameterSource()
			.addValue("limit", communityPage.reviewOffset() == 0 ? PAGE_SIZE : PAGE_SIZE - 1)
			.addValue("reviewOffset", communityPage.reviewOffset() != 0 ? communityPage.reviewOffset() - 1 : 0)
			.addValue("userId", userId);

		final List<CommunityCardInfo> reviewCards =
			jdbcTemplate.query(
				getReviewBaseSql(""),
				params,
				COMMUNITY_CARD_INFO_ROW_MAPPER
			);

		if (communityPage.reviewOffset() == 0) {
			getFixedEvent(userId)
				.ifPresent(fixedEvent -> reviewCards.add(0, fixedEvent));
		}

		return new PageImpl<>(
			reviewCards,
			PageRequest.of(communityPage.page(), PAGE_SIZE),
			getReviewCardsCount(userId)
		);
	}

	public List<CommunityCardInfo> findHotPosts(final Long userId) {
		List<CommunityCardInfo> hotPostsWithEvent = new ArrayList<>();
		getFixedEvent(userId).ifPresent(hotPostsWithEvent::add);

		final List<HotCommunityRank> communityTopScoresWithIds = getCommunityTopScoresWithId(userId);

		List<Long> topReviewIds = new ArrayList<>();
		List<Long> topPostIds = new ArrayList<>();

		for (int i = 0; i < Math.min(communityTopScoresWithIds.size(), 2); i++) {
			HotCommunityRank rank = communityTopScoresWithIds.get(i);
			if (rank.isReview) {
				topReviewIds.add(rank.id);
			} else {
				topPostIds.add(rank.id);
			}
		}

		if (!topReviewIds.isEmpty()) {
			hotPostsWithEvent.addAll(fetchTopReviews(userId, topReviewIds));
		}

		if (!topPostIds.isEmpty()) {
			hotPostsWithEvent.addAll(fetchTopPosts(userId, topPostIds));
		}
		return hotPostsWithEvent;
	}

	private List<CommunityCardInfo> fetchTopPosts(final Long userId, final List<Long> topPostIds) {
		final MapSqlParameterSource params = new MapSqlParameterSource()
			.addValue("limit", topPostIds.size())
			.addValue("postOffset", 0)
			.addValue("userId", userId);
		return jdbcTemplate.query(
			getPostBaseSqlWithWhereClaus(
				String.format("t1.id in ( %s )",
					topPostIds.stream().map(String::valueOf).collect(Collectors.joining(",")))
			),
			params,
			COMMUNITY_CARD_INFO_ROW_MAPPER
		);
	}

	private List<CommunityCardInfo> fetchTopReviews(final Long userId, final List<Long> topReviewIds) {
		final MapSqlParameterSource params = new MapSqlParameterSource()
			.addValue("limit", topReviewIds.size())
			.addValue("reviewOffset", 0)
			.addValue("userId", userId);

		return jdbcTemplate.query(
			getReviewBaseSql(
				String.format("and (t1.id in ( %s ))",
					topReviewIds.stream().map(String::valueOf).collect(Collectors.joining(",")))
			),
			params,
			COMMUNITY_CARD_INFO_ROW_MAPPER
		);
	}

	private List<HotCommunityRank> getCommunityTopScoresWithId(final Long userId) {
		final List<Tuple> topReviewScores = getTopThreeReviewScoresWithId(userId);
		final List<Tuple> topPostScores = getTopThreePostScoresWIthId(userId);

		final List<HotCommunityRank> combinedCommunityTopScores = getHotCommunityRanks(false, topPostScores);
		combinedCommunityTopScores.addAll(getHotCommunityRanks(true, topReviewScores));

		return combinedCommunityTopScores.stream()
			.sorted(Comparator.comparing(HotCommunityRank::score)
				.thenComparing(HotCommunityRank::createdAt).reversed())
			.toList();
	}

	private List<Tuple> getTopThreePostScoresWIthId(final Long userId) {
		return queryFactory.select(post.id,
				postLike.count().add(comment.count()).as("score"),
				post.createdAt)
			.from(post)
			.leftJoin(postLike)
			.on(post.id.eq(postLike.post.id)
				.and(postLike.createdAt.between(LocalDateTime.now().minusDays(3), LocalDateTime.now())))
			.leftJoin(comment)
			.on(post.id.eq(comment.post.id)
				.and(comment.createdAt.between(LocalDateTime.now().minusDays(3), LocalDateTime.now())))
			.leftJoin(blockUser).on(blockUser.toUser.id.eq(post.user.id).and(blockUser.fromUser.id.eq(userId)))
			.where(post.postTopic.ne(PostTopic.EVENT).and(blockUser.isNull()))
			.groupBy(post)
			.orderBy(postLike.count().add(comment.count()).desc(), post.createdAt.desc())
			.limit(3)
			.fetch();
	}

	private List<Tuple> getTopThreeReviewScoresWithId(final Long userId) {
		return queryFactory.select(review.id,
				reviewLike.count().add(reviewComment.count()).as("score"),
				review.createdAt)
			.from(review)
			.leftJoin(reviewLike)
			.on(review.id.eq(reviewLike.review.id)
				.and(reviewLike.createdAt.between(LocalDateTime.now().minusDays(3), LocalDateTime.now())))
			.leftJoin(reviewComment)
			.on(review.id.eq(reviewComment.review.id)
				.and(reviewComment.createdAt.between(LocalDateTime.now().minusDays(3), LocalDateTime.now())))
			.leftJoin(blockUser).on(blockUser.toUser.id.eq(review.user.id).and(blockUser.fromUser.id.eq(userId)))
			.where(blockUser.isNull())
			.groupBy(review)
			.orderBy(reviewLike.count().add(reviewComment.count()).desc(), review.createdAt.desc())
			.limit(3)
			.fetch();
	}

	private List<HotCommunityRank> getHotCommunityRanks(final boolean isReview, final List<Tuple> queryResult) {
		return queryResult.stream()
			.map(tuple ->
				new HotCommunityRank(
					isReview,
					tuple.get(0, Long.class),
					tuple.get(1, Long.class),
					tuple.get(2, LocalDateTime.class)
				)
			)
			.collect(Collectors.toList());
	}

	private record HotCommunityRank(
		boolean isReview,
		Long id,
		Long score,
		LocalDateTime createdAt
	) {

	}

	private Optional<CommunityCardInfo> getFixedEvent(final Long userId) {
		final MapSqlParameterSource fixedEventParams = new MapSqlParameterSource()
			.addValue("limit", 1)
			.addValue("postOffset", 0)
			.addValue("userId", userId);
		final String fixedEventSql = getPostBaseSqlWithWhereClaus(
			"(t1.post_topic = 'EVENT')");
		final List<CommunityCardInfo> result =
			jdbcTemplate.query(fixedEventSql, fixedEventParams, COMMUNITY_CARD_INFO_ROW_MAPPER);
		return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
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
				 , case when t6.count >= 0 then true
				  		else false
				  end 							 as isUserLiked	
				 , case when t5.count >= 0 then true
				  		else false
				  	end 							 as isUserCommented
				
			   from post t1
			   inner join user t2 on t1.user_id = t2.id
			   left join post_manager_mapper t4 on t1.id = t4.post_id 
			   									and t4.is_posted is true
			   									and t4.is_fixed is true
			   left join (select to_user_id 
			   			  from block_user
			   			  where from_user_id = :userId) t3 on t1.user_id = t3.to_user_id	
			   left join (select post_id, count(post_id) as count
							from comment
							where user_id = :userId 
							group by post_id ) t5 on t1.id = t5.post_id
			   left join (select post_id, count(post_id)  as count
							from post_like
							where user_id = :userId 
							group by post_id ) t6 on t1.id = t6.post_id							
			   where %s
			   and t3.to_user_id is null
			   order by t4.is_fixed desc, t1.created_at desc, t1.id desc
			   limit :limit offset :postOffset 
			""", whereClause);
	}

	private String getReviewBaseSql(final String whereClause) {
		return String.format("""
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
				     , case when t6.count >= 0 then true
							else false
				  		end 							 as isUserCommented	
				 	 , case when t5.count >= 0 then true
				  			else false
				  		end 							 as isUserLiked
				from review t1
			    inner join user t2 on t1.user_id = t2.id
			    inner join bakery t3 on t1.bakery_id = t3.id
			    left join (select to_user_id 
						  from block_user
						  where from_user_id = :userId) t4 on t1.user_id = t4.to_user_id	
				left join (select review_id, count(review_id) as count
							from review_like
							where user_id = :userId 
							group by review_id ) t5 on t1.id = t5.review_id
				left join (select review_id, count(review_id) as count
							from review_comment
							where user_id = :userId 
							group by review_id ) t6 on t1.id = t6.review_id								
			   where t4.to_user_id is null
			   %s
				order by t1.created_at desc , t1.id desc
				limit :limit offset :reviewOffset		
			""", whereClause);
	}

	private Long getAllCardsCount(final Long userId) {
		final String sql = """
			select sum(total_count)
			from (
				   select count(t1.id) AS total_count
				   from review t1
				   left join (select to_user_id 
							  from block_user
							  where from_user_id = :userId) t2 on t1.user_id = t2.to_user_id			
				   where t2.to_user_id is null
				   
				   union all
			   
				   select count(t1.id) AS total_count
				   from post t1
				   left join (select to_user_id 
							  from block_user
							  where from_user_id = :userId) t2 on t1.user_id = t2.to_user_id	
					left join post_manager_mapper t3 on t1.id = t3.post_id 
			   									and t3.is_posted is true 
				   where t2.to_user_id is null
			   ) total
			""";
		final MapSqlParameterSource param = new MapSqlParameterSource()
			.addValue("userId", userId);
		return jdbcTemplate.queryForObject(sql, param, Long.class);
	}

	private Long getPostsCardsCount(final PostTopic postTopic, final int postOffset, final Long userId) {
		final String sql = """
			select count(t1.id) + :postOffset
			from post t1
			left join (select to_user_id 
			 			  from block_user
			 			  where from_user_id = :userId) t2 on t1.user_id = t2.to_user_id		
			left join post_manager_mapper t3 on t1.id = t3.post_id 
			   									and t3.is_posted is true	
			where post_topic = :postTopic
			and t2.to_user_id is null
			""";
		final MapSqlParameterSource param = new MapSqlParameterSource()
			.addValue("postTopic", postTopic.name())
			.addValue("postOffset", postOffset)
			.addValue("userId", userId);
		return jdbcTemplate.queryForObject(sql, param, Long.class);
	}

	private Long getReviewCardsCount(final Long userId) {
		final String sql = """
			select count(t1.id) + 1
			from review t1
			   left join (select to_user_id 
			 			  from block_user
			 			  where from_user_id = :userId) t2 on t1.user_id = t2.to_user_id										
			    where t2.to_user_id is null
			""";
		final MapSqlParameterSource param = new MapSqlParameterSource()
			.addValue("userId", userId);
		return jdbcTemplate.queryForObject(sql, param, Long.class);
	}

}
