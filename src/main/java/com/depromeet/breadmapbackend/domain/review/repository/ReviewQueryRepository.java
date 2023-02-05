package com.depromeet.breadmapbackend.domain.review.repository;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.exception.DaedongException;
import com.depromeet.breadmapbackend.domain.exception.DaedongStatus;
import com.depromeet.breadmapbackend.domain.product.Product;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewSortType;
import com.depromeet.breadmapbackend.domain.review.ReviewStatus;
import com.depromeet.breadmapbackend.domain.user.User;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.depromeet.breadmapbackend.domain.review.QReview.review;
import static com.depromeet.breadmapbackend.domain.review.QReviewProductRating.reviewProductRating;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final Integer BAKERY_REVIEW_SIZE = 5;
    private final Integer PRODUCT_REVIEW_SIZE = 5;
    private final Integer USER_REVIEW_SIZE = 5;

    public Page<Review> findBakeryReview(Bakery bakery, ReviewSortType sortBy, Long lastId, Double lastRating, int page) {
        Pageable pageable = PageRequest.of(page, BAKERY_REVIEW_SIZE);

        List<Review> content = queryFactory.selectFrom(review)
                .where(review.bakery.eq(bakery),
                        review.status.eq(ReviewStatus.UNBLOCK))
                .join(review.ratings, reviewProductRating) // TODO
                .groupBy(reviewProductRating.review.id)
                .having(infinityCondition(sortBy, lastId, lastRating, page))
                .orderBy(orderType(sortBy), review.createdAt.desc())
                .offset(page)
                .limit(BAKERY_REVIEW_SIZE) // TODO
                .fetch();

        Long count = queryFactory.select(review.count()).from(review)
                .where(review.bakery.eq(bakery),
                        review.status.eq(ReviewStatus.UNBLOCK))
                .join(review.ratings, reviewProductRating) // TODO
                .groupBy(reviewProductRating.review.id)
                .having(infinityCondition(sortBy, lastId, lastRating, page))
                .fetchOne();

        return new PageImpl<>(content, pageable, count);
    }

    public Page<Review> findProductReview(Bakery bakery, Product product, ReviewSortType sortBy, Long lastId, Double lastRating, int page) {
        Pageable pageable = PageRequest.of(page, PRODUCT_REVIEW_SIZE);

        List<Review> content = queryFactory.selectFrom(review)
                .where(review.bakery.eq(bakery),
                        reviewProductRating.product.eq(product),
                        review.status.eq(ReviewStatus.UNBLOCK))
                .join(review.ratings, reviewProductRating) // TODO
                .groupBy(reviewProductRating.review.id)
                .having(infinityCondition(sortBy, lastId, lastRating, page))
                .orderBy(orderType(sortBy), review.createdAt.desc())
                .offset(page)
                .limit(PRODUCT_REVIEW_SIZE) // TODO
                .fetch();

        Long count = queryFactory.select(review.count()).from(review)
                .where(review.bakery.eq(bakery),
                        reviewProductRating.product.eq(product),
                        review.status.eq(ReviewStatus.UNBLOCK))
                .join(review.ratings, reviewProductRating) // TODO
                .groupBy(reviewProductRating.review.id)
                .having(infinityCondition(sortBy, lastId, lastRating, page))
                .fetchOne();

        return new PageImpl<>(content, pageable, count);
    }

    public Page<Review> findUserReview(User user, int page) {
        Pageable pageable = PageRequest.of(page, USER_REVIEW_SIZE);

        List<Review> content = queryFactory.selectFrom(review)
                .where(review.user.eq(user),
                        review.status.eq(ReviewStatus.UNBLOCK))
                .join(review.ratings, reviewProductRating) // TODO
                .groupBy(reviewProductRating.review.id)
                .orderBy(review.createdAt.desc())
                .offset(page)
                .limit(USER_REVIEW_SIZE) // TODO
                .fetch();

        Long count = queryFactory.select(review.count()).from(review)
                .where(review.user.eq(user),
                        review.status.eq(ReviewStatus.UNBLOCK))
                .join(review.ratings, reviewProductRating) // TODO
                .groupBy(reviewProductRating.review.id)
                .fetchOne();

        return new PageImpl<>(content, pageable, count);
    }

    private BooleanExpression infinityCondition(ReviewSortType sortBy, Long lastId, Double lastRating, int page) {
        if (sortBy.equals(ReviewSortType.LATEST)){
            if (lastId == null) {
                if (page == 0) return null;
                else throw new DaedongException(DaedongStatus.REVIEW_PAGE_EXCEPTION);
            }
            return review.id.lt(lastId);
        }
        else if (sortBy.equals(ReviewSortType.HIGH)) {
            if (lastRating == null) {
                if (page == 0) return null;
                else throw new DaedongException(DaedongStatus.REVIEW_PAGE_EXCEPTION);
            }
            return reviewProductRating.rating.avg().lt(lastRating);
        }
        else if (sortBy.equals(ReviewSortType.LOW)) {
            if (lastRating == null) {
                if (page == 0) return null;
                else throw new DaedongException(DaedongStatus.REVIEW_PAGE_EXCEPTION);
            }
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
