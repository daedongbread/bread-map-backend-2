package com.depromeet.breadmapbackend.domain.review;

import static com.depromeet.breadmapbackend.domain.bakery.QBakery.*;
import static com.depromeet.breadmapbackend.domain.review.QReview.*;
import static com.depromeet.breadmapbackend.domain.review.QReviewProductRating.*;
import static com.depromeet.breadmapbackend.domain.user.block.QBlockUser.*;
import static com.querydsl.core.group.GroupBy.*;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryStatus;
import com.depromeet.breadmapbackend.domain.bakery.product.Product;
import com.depromeet.breadmapbackend.domain.search.dto.BakeryReviewScoreDto;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewQueryRepository {
	private final JPAQueryFactory queryFactory;
	private final int BAKERY_REVIEW_SIZE = 5;
	private final int PRODUCT_REVIEW_SIZE = 5;
	private final int USER_REVIEW_SIZE = 5;

	public List<Review> findByUserIdAndBakery(Long userId, Bakery targetBakery) {
		return queryFactory
			.selectFrom(review)
			.join(review.bakery, bakery).fetchJoin()
			.join(review.ratings, reviewProductRating).fetchJoin()
			.where(review.isDelete.isFalse(), // 리뷰 삭제 여부
				review.isBlock.isFalse(), // 리뷰 차단 여부
				review.bakery.eq(targetBakery), // 해당 빵집 리뷰
				review.user.notIn( // 리뷰 유처 차단 여부
					JPAExpressions.select(blockUser.toUser)
						.from(blockUser)
						.where(blockUser.fromUser.id.eq(userId))
				))
			.fetch();
	}

	public Map<Long, List<Review>> findReviewListInBakeries(final Long userId, final List<Bakery> bakeries) {
		return queryFactory
			.selectFrom(review)
			.join(review.bakery, bakery).fetchJoin()
			.where(review.isDelete.isFalse(),
				review.isBlock.isFalse(),
				review.user.id.eq(userId),
				review.user.notIn( // 리뷰 유처 차단 여부
					JPAExpressions.select(blockUser.toUser)
						.from(blockUser)
						.where(blockUser.fromUser.id.eq(userId))
				),
				bakery.in(bakeries))
			.transform(groupBy(review.bakery.id).as(list(review)));

	}

	public List<Review> findReviewList(User me, Bakery targetBakery) {
		return queryFactory.selectFrom(review)
			.join(review.bakery, bakery).fetchJoin() // TODO
			.where(review.isDelete.isFalse(), // 리뷰 삭제 여부
				review.isBlock.isFalse(), // 리뷰 차단 여부
				review.bakery.eq(targetBakery), // 해당 빵집 리뷰
				review.user.notIn( // 리뷰 유처 차단 여부
					JPAExpressions.select(blockUser.toUser)
						.from(blockUser)
						.where(blockUser.fromUser.eq(me))
				))
			.fetch();
	}

	public Page<Review> findBakeryReview(User me, Bakery bakery, ReviewSortType sortBy, int page) {
		// 해당 유저가 page=0을 호출했을때 레디스에 지금 시간 저장, 이후 page들에서 사용
		Pageable pageable = PageRequest.of(page, BAKERY_REVIEW_SIZE);

		//        if (page == 0) { // TODO 근데 그럼 하나의 빵집에 대해서만 보장 가능
		//            redisTemplate.opsForValue()
		//                    .set(customRedisProperties.getKey().getBakeryReview() + ":" + me.getId(), String.valueOf(LocalDateTime.now()));
		//        }
		//        String firstTimeString = redisTemplate.opsForValue().get(customRedisProperties.getKey().getBakeryReview() + ":" + me.getId());
		//        LocalDateTime firstTime;
		//        if (firstTimeString == null) throw new DaedongException(DaedongStatus.REVIEW_PAGE_EXCEPTION);
		//        else firstTime = LocalDateTime.parse(firstTimeString);

		List<Review> content = queryFactory.selectFrom(review)
			.leftJoin(review.ratings, reviewProductRating)//.fetchJoin() // TODO
			.where(review.user.notIn(
					JPAExpressions.select(blockUser.toUser)
						.from(blockUser)
						.where(blockUser.fromUser.eq(me))
				),
				review.bakery.status.eq(BakeryStatus.POSTING),
				review.bakery.eq(bakery),
				review.isDelete.isFalse(),
				review.isBlock.isFalse())
			//review.createdAt.before(firstTime))
			.groupBy(review.id)
			.orderBy(orderType(sortBy), review.createdAt.desc())
			.offset((long)page * BAKERY_REVIEW_SIZE)
			.limit(BAKERY_REVIEW_SIZE)
			.fetch();

		Long count = queryFactory.select(review.count()).from(review)
			.where(review.user.notIn(
					JPAExpressions.select(blockUser.toUser)
						.from(blockUser)
						.where(blockUser.fromUser.eq(me))
				),
				review.bakery.status.eq(BakeryStatus.POSTING),
				review.bakery.eq(bakery),
				review.isDelete.isFalse(),
				review.isBlock.isFalse())
			//review.createdAt.before(firstTime))
			.fetchOne();

		return new PageImpl<>(content, pageable, count);
	}

	public Page<Review> findProductReview(User me, Bakery bakery, Product product, ReviewSortType sortBy, int page) {
		Pageable pageable = PageRequest.of(page, PRODUCT_REVIEW_SIZE);

		//        if (page == 0) {
		//            redisTemplate.opsForValue()
		//                    .set(customRedisProperties.getKey().getProductReview() + ":" + me.getId(), String.valueOf(LocalDateTime.now()));
		//        }
		//        String firstTimeString = redisTemplate.opsForValue().get(customRedisProperties.getKey().getProductReview() + ":" + me.getId());
		//        LocalDateTime firstTime;
		//        if (firstTimeString == null) throw new DaedongException(DaedongStatus.REVIEW_PAGE_EXCEPTION);
		//        else firstTime = LocalDateTime.parse(firstTimeString);

		List<Review> content = queryFactory.selectFrom(review)
			.leftJoin(review.ratings, reviewProductRating)//.fetchJoin() // TODO
			.where(review.user.notIn(
					JPAExpressions.select(blockUser.toUser)
						.from(blockUser)
						.where(blockUser.fromUser.eq(me))
				),
				review.bakery.status.eq(BakeryStatus.POSTING),
				review.bakery.eq(bakery),
				reviewProductRating.product.eq(product),
				review.isDelete.isFalse(),
				review.isBlock.isFalse())
			//review.createdAt.before(firstTime))
			.groupBy(review.id)//, reviewProductRating.review.id)
			.orderBy(orderType(sortBy), review.createdAt.desc())
			.offset((long)page * PRODUCT_REVIEW_SIZE)
			.limit(PRODUCT_REVIEW_SIZE)
			.fetch();

		Long count = queryFactory.select(review.count()).from(review)
			.leftJoin(review.ratings, reviewProductRating)//.fetchJoin() // TODO
			.where(review.user.notIn(
					JPAExpressions.select(blockUser.toUser)
						.from(blockUser)
						.where(blockUser.fromUser.eq(me))
				),
				review.bakery.status.eq(BakeryStatus.POSTING),
				review.bakery.eq(bakery),
				reviewProductRating.product.eq(product),
				review.isDelete.isFalse(),
				review.isBlock.isFalse())
			//review.createdAt.before(firstTime))
			.fetchOne();

		return new PageImpl<>(content, pageable, count);
	}

	public Page<Review> findUserReview(User me, User user, int page) {
		Pageable pageable = PageRequest.of(page, USER_REVIEW_SIZE);

		//        if (page == 0) {
		//            redisTemplate.opsForValue()
		//                    .set(customRedisProperties.getKey().getUserReview() + ":" + me.getId(), String.valueOf(LocalDateTime.now()));
		//        }
		//        String firstTimeString = redisTemplate.opsForValue().get(customRedisProperties.getKey().getUserReview() + ":" + me.getId());
		//        LocalDateTime firstTime;
		//        if (firstTimeString == null) throw new DaedongException(DaedongStatus.REVIEW_PAGE_EXCEPTION);
		//        else firstTime = LocalDateTime.parse(firstTimeString);

		List<Review> content = queryFactory.selectFrom(review)
			.where(review.user.notIn(
					JPAExpressions.select(blockUser.toUser)
						.from(blockUser)
						.where(blockUser.fromUser.eq(me))
				),
				review.bakery.status.eq(BakeryStatus.POSTING),
				review.user.eq(user),
				review.isDelete.isFalse(),
				review.isBlock.isFalse())
			//review.createdAt.before(firstTime))
			.orderBy(review.createdAt.desc())
			.offset((long)page * USER_REVIEW_SIZE)
			.limit(USER_REVIEW_SIZE)
			.fetch();

		int count = Math.toIntExact(queryFactory.select(review.id.countDistinct())
			.from(review)
			.where(review.user.notIn(
					JPAExpressions.select(blockUser.toUser)
						.from(blockUser)
						.where(blockUser.fromUser.eq(me))
				),
				review.bakery.status.eq(BakeryStatus.POSTING),
				review.user.eq(user),
				review.isDelete.isFalse(),
				review.isBlock.isFalse())
			//review.createdAt.before(firstTime))
			.fetchFirst());

		return new PageImpl<>(content, pageable, count);
	}

	private BooleanExpression infinityCondition(ReviewSortType sortBy, Long lastId, Double lastRating, int page) {
		if (page == 0)
			return null;
		if (sortBy.equals(ReviewSortType.LATEST)) {
			if (lastId == null)
				throw new DaedongException(DaedongStatus.REVIEW_PAGE_EXCEPTION);
			return review.id.lt(lastId);
		} else if (sortBy.equals(ReviewSortType.HIGH)) {
			if (lastRating == null)
				throw new DaedongException(DaedongStatus.REVIEW_PAGE_EXCEPTION);
			return reviewProductRating.rating.avg().lt(lastRating);
		} else if (sortBy.equals(ReviewSortType.LOW)) {
			if (lastRating == null)
				throw new DaedongException(DaedongStatus.REVIEW_PAGE_EXCEPTION);
			return reviewProductRating.rating.avg().gt(lastRating);
		} else
			throw new DaedongException(DaedongStatus.REVIEW_SORT_TYPE_EXCEPTION);
	}

	private OrderSpecifier<?> orderType(ReviewSortType sortBy) {
		if (sortBy.equals(ReviewSortType.LATEST)) {
			return review.createdAt.desc();
		} else if (sortBy.equals(ReviewSortType.HIGH)) {
			return reviewProductRating.rating.avg().desc();
		} else if (sortBy.equals(ReviewSortType.LOW)) {
			return reviewProductRating.rating.avg().asc();
		} else
			throw new DaedongException(DaedongStatus.REVIEW_SORT_TYPE_EXCEPTION);
	}

	public List<BakeryReviewScoreDto> getBakeriesReview(List<Long> bakeryIds) {
		return queryFactory
			.select(
				Projections.constructor(
					BakeryReviewScoreDto.class,
					review.bakery.id.as("bakeryId"),
					reviewProductRating.rating.avg().coalesce(0d).as("totalScore"),
					review.count().coalesce(0L).as("reviewCount")
				)
			)
			.from(review)
			.innerJoin(reviewProductRating).on(review.id.eq(reviewProductRating.review.id))
			.where(review.bakery.id.in(bakeryIds))
			.groupBy(review.bakery.id)
			.fetch();
	}

}
