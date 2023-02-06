package com.depromeet.breadmapbackend.domain.review.repository;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.exception.DaedongException;
import com.depromeet.breadmapbackend.domain.exception.DaedongStatus;
import com.depromeet.breadmapbackend.domain.product.Product;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewSortType;
import com.depromeet.breadmapbackend.domain.review.ReviewStatus;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.infra.properties.CustomRedisProperties;
import com.depromeet.breadmapbackend.security.token.JwtToken;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.depromeet.breadmapbackend.domain.review.QReview.review;
import static com.depromeet.breadmapbackend.domain.review.QReviewProductRating.reviewProductRating;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final StringRedisTemplate redisTemplate;
    private final CustomRedisProperties customRedisProperties;
    private final int BAKERY_REVIEW_SIZE = 5;
    private final int PRODUCT_REVIEW_SIZE = 5;
    private final int USER_REVIEW_SIZE = 5;

    public Page<Review> findBakeryReview(User me, Bakery bakery, ReviewSortType sortBy, int page) {
        // 해당 유저가 page=0을 호출했을때 레디스에 지금 시간 저장, 이후 page들에서 사용
        Pageable pageable = PageRequest.of(page, BAKERY_REVIEW_SIZE);

        if (page == 0) { // TODO 근데 그럼 하나의 빵집에 대해서만 보장 가능
            redisTemplate.opsForValue()
                    .set(customRedisProperties.getKey().getBakeryReview() + ":" + me.getId(), String.valueOf(LocalDateTime.now()));
        }
        String firstTimeString = redisTemplate.opsForValue().get(customRedisProperties.getKey().getBakeryReview() + ":" + me.getId());
        LocalDateTime firstTime;
        if (firstTimeString == null) throw new DaedongException(DaedongStatus.REVIEW_PAGE_EXCEPTION);
        else firstTime = LocalDateTime.parse(firstTimeString);

        List<Review> content = queryFactory.selectFrom(review)
                .leftJoin(review.ratings, reviewProductRating)//.fetchJoin() // TODO
                .where(review.bakery.eq(bakery),
                        review.status.eq(ReviewStatus.UNBLOCK),
                        review.createdAt.before(firstTime))
                .groupBy(reviewProductRating.review.id)
                .orderBy(orderType(sortBy), review.createdAt.desc())
                .offset((long) page * BAKERY_REVIEW_SIZE)
                .limit(BAKERY_REVIEW_SIZE)
                .fetch();

        Long count = queryFactory.select(review.count()).from(review)
                .where(review.bakery.eq(bakery),
                        review.status.eq(ReviewStatus.UNBLOCK),
                        review.createdAt.before(firstTime))
                .fetchOne();

        return new PageImpl<>(content, pageable, count);
    }

    public Page<Review> findProductReview(User me, Bakery bakery, Product product, ReviewSortType sortBy, int page) {
        Pageable pageable = PageRequest.of(page, PRODUCT_REVIEW_SIZE);

        if (page == 0) {
            redisTemplate.opsForValue()
                    .set(customRedisProperties.getKey().getProductReview() + ":" + me.getId(), String.valueOf(LocalDateTime.now()));
        }
        String firstTimeString = redisTemplate.opsForValue().get(customRedisProperties.getKey().getProductReview() + ":" + me.getId());
        LocalDateTime firstTime;
        if (firstTimeString == null) throw new DaedongException(DaedongStatus.REVIEW_PAGE_EXCEPTION);
        else firstTime = LocalDateTime.parse(firstTimeString);

        List<Review> content = queryFactory.selectFrom(review)
                .leftJoin(review.ratings, reviewProductRating)//.fetchJoin() // TODO
                .where(review.bakery.eq(bakery),
                        reviewProductRating.product.eq(product),
                        review.status.eq(ReviewStatus.UNBLOCK),
                        review.createdAt.before(firstTime))
                .groupBy(reviewProductRating.review.id)
                .orderBy(orderType(sortBy), review.createdAt.desc())
                .offset((long) page * PRODUCT_REVIEW_SIZE)
                .limit(PRODUCT_REVIEW_SIZE)
                .fetch();

        Long count = queryFactory.select(review.count()).from(review)
                .leftJoin(review.ratings, reviewProductRating)//.fetchJoin() // TODO
                .where(review.bakery.eq(bakery),
                        reviewProductRating.product.eq(product),
                        review.status.eq(ReviewStatus.UNBLOCK),
                        review.createdAt.before(firstTime))
                .groupBy(reviewProductRating.review.id)
                .fetchOne();

        return new PageImpl<>(content, pageable, count);
    }

    public Page<Review> findUserReview(User me, User user, int page) {
        Pageable pageable = PageRequest.of(page, USER_REVIEW_SIZE);

        if (page == 0) {
            redisTemplate.opsForValue()
                    .set(customRedisProperties.getKey().getUserReview() + ":" + me.getId(), String.valueOf(LocalDateTime.now()));
        }
        String firstTimeString = redisTemplate.opsForValue().get(customRedisProperties.getKey().getUserReview() + ":" + me.getId());
        LocalDateTime firstTime;
        if (firstTimeString == null) throw new DaedongException(DaedongStatus.REVIEW_PAGE_EXCEPTION);
        else firstTime = LocalDateTime.parse(firstTimeString);

        List<Review> content = queryFactory.selectFrom(review)
                .where(review.user.eq(user),
                        review.status.eq(ReviewStatus.UNBLOCK),
                        review.createdAt.before(firstTime))
                .orderBy(review.createdAt.desc())
                .offset((long) page * USER_REVIEW_SIZE)
                .limit(USER_REVIEW_SIZE)
                .fetch();

        Long count = queryFactory.select(review.count()).from(review)
                .where(review.user.eq(user),
                        review.status.eq(ReviewStatus.UNBLOCK),
                        review.createdAt.before(firstTime))
                .fetchOne();

        return new PageImpl<>(content, pageable, count);
    }

    private BooleanExpression infinityCondition(ReviewSortType sortBy, Long lastId, Double lastRating, int page) {
        if (page == 0) return null;
        if (sortBy.equals(ReviewSortType.LATEST)){
            if (lastId == null) throw new DaedongException(DaedongStatus.REVIEW_PAGE_EXCEPTION);
            return review.id.lt(lastId);
        }
        else if (sortBy.equals(ReviewSortType.HIGH)) {
            if (lastRating == null) throw new DaedongException(DaedongStatus.REVIEW_PAGE_EXCEPTION);
            return reviewProductRating.rating.avg().lt(lastRating);
        }
        else if (sortBy.equals(ReviewSortType.LOW)) {
            if (lastRating == null) throw new DaedongException(DaedongStatus.REVIEW_PAGE_EXCEPTION);
            return reviewProductRating.rating.avg().gt(lastRating);
        }
        else throw new DaedongException(DaedongStatus.REVIEW_SORT_TYPE_EXCEPTION);
    }

    private OrderSpecifier<?> orderType(ReviewSortType sortBy) {
        if (sortBy.equals(ReviewSortType.LATEST)){
            return review.createdAt.desc();
        }
        else if (sortBy.equals(ReviewSortType.HIGH)) {
            return reviewProductRating.rating.avg().desc();
        }
        else if (sortBy.equals(ReviewSortType.LOW)) {
            return reviewProductRating.rating.avg().asc();
        }
        else throw new DaedongException(DaedongStatus.REVIEW_SORT_TYPE_EXCEPTION);
    }
}
