package com.depromeet.breadmapbackend.domain.review;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.review.view.ReviewView;
import com.depromeet.breadmapbackend.domain.review.view.ReviewViewRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.domain.bakery.product.Product;
import com.depromeet.breadmapbackend.domain.bakery.BakeryRepository;
import com.depromeet.breadmapbackend.domain.bakery.product.ProductRepository;
import com.depromeet.breadmapbackend.domain.review.like.ReviewLikeRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.follow.FollowRepository;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;
import com.depromeet.breadmapbackend.domain.review.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewQueryRepository reviewQueryRepository;
    private final ReviewViewRepository reviewViewRepository;
    private final UserRepository userRepository;
    private final BakeryRepository bakeryRepository;
    private final ProductRepository productRepository;
    private final ReviewProductRatingRepository reviewProductRatingRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final FollowRepository followRepository;

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageResponseDto<ReviewDto> getBakeryReviewList(String oAuthId, Long bakeryId, ReviewSortType sortBy, int page) {
        User me = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));

        Page<Review> bakeryReviews = reviewQueryRepository.findBakeryReview(me, bakery, sortBy, page);
        List<ReviewDto> contents = bakeryReviews.getContent().stream()
                .map(review -> new ReviewDto(review,
                        reviewRepository.countByUser(review.getUser()),
                        followRepository.countByToUser(review.getUser()),
                        followRepository.findByFromUserAndToUser(me, review.getUser()).isPresent(),
                        me.equals(review.getUser()),
                        reviewLikeRepository.findByUserAndReview(me, review).isPresent()))
                .collect(Collectors.toList());
        return PageResponseDto.of(bakeryReviews, contents);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageResponseDto<ReviewDto> getProductReviewList(String oAuthId, Long bakeryId, Long productId, ReviewSortType sortBy, int page) {
        User me = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        Product product = productRepository.findByIdAndBakery(productId, bakery).orElseThrow(() -> new DaedongException(DaedongStatus.PRODUCT_NOT_FOUND));

        Page<Review> productReviews = reviewQueryRepository.findProductReview(me, bakery, product, sortBy, page);
        List<ReviewDto> contents = productReviews.getContent().stream()
                .map(review -> new ReviewDto(review,
                        reviewRepository.countByUser(review.getUser()),
                        followRepository.countByToUser(review.getUser()),
                        followRepository.findByFromUserAndToUser(me, review.getUser()).isPresent(),
                        me.equals(review.getUser()),
                        reviewLikeRepository.findByUserAndReview(me, review).isPresent()))
                .collect(Collectors.toList());
        return PageResponseDto.of(productReviews, contents);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageResponseDto<ReviewDto> getUserReviewList(String oAuthId, Long userId, int page) {
        User me = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        User user = userRepository.findById(userId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));

        Page<Review> userReviews = reviewQueryRepository.findUserReview(me, user, page);
        List<ReviewDto> contents = userReviews.getContent().stream()
                .map(review -> new ReviewDto(review,
                        reviewRepository.countByUser(review.getUser()),
                        followRepository.countByToUser(review.getUser()),
                        followRepository.findByFromUserAndToUser(me, review.getUser()).isPresent(),
                        me.equals(review.getUser()),
                        reviewLikeRepository.findByUserAndReview(me, review).isPresent()))
                .collect(Collectors.toList());
        return PageResponseDto.of(userReviews, contents);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReviewDetailDto getReview(String oAuthId, Long reviewId) {
        User user = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Review review = reviewRepository.findById(reviewId)
                .filter(r -> !r.getIsBlock()).orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_NOT_FOUND));
        reviewViewRepository.findByReview(review)
                .orElseGet(() -> {
                    ReviewView reviewView = ReviewView.builder().review(review).build();
                    return reviewViewRepository.save(reviewView);
                }).viewReview();

        List<SimpleReviewDto> userOtherReviews = reviewRepository.findByUser(review.getUser()).stream()
                .sorted(Comparator.comparing(Review::getCreatedAt).reversed())
                .limit(5).map(SimpleReviewDto::new).collect(Collectors.toList());

        List<SimpleReviewDto> bakeryOtherReviews = reviewRepository.findByBakery(review.getBakery()).stream()
                .sorted(Comparator.comparing(Review::getCreatedAt).reversed())
                .limit(5).map(SimpleReviewDto::new).collect(Collectors.toList());

        return ReviewDetailDto.builder()
                .review(review).reviewNum(reviewRepository.countByUser(review.getUser()))
                .followerNum(followRepository.countByToUser(review.getUser()))
                .isFollow(followRepository.findByFromUserAndToUser(user, review.getUser()).isPresent())
                .isMe(user.equals(review.getUser()))
                .isLike(reviewLikeRepository.findByUserAndReview(user, review).isPresent())
                .userOtherReviews(userOtherReviews)
                .bakeryOtherReviews(bakeryOtherReviews)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void addReview(String oAuthId, Long bakeryId, ReviewRequest request) {
        User user = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));

        Review review = Review.builder()
                .user(user).bakery(bakery).content(request.getContent())/*.isUse(true)*/.build();
        reviewRepository.save(review);
        reviewViewRepository.save(ReviewView.builder().review(review).build());

        if ((request.getProductRatingList() == null && request.getProductRatingList().isEmpty()) && (request.getNoExistProductRatingRequestList() == null && request.getNoExistProductRatingRequestList().isEmpty()))
            throw new DaedongException(DaedongStatus.REVIEW_NEED_PRODUCT_EXCEPTION);

        if(request.getProductRatingList() != null && !request.getProductRatingList().isEmpty()) {
            request.getProductRatingList().forEach(productRatingRequest -> {
                Product product = productRepository.findById(productRatingRequest.getProductId()).orElseThrow(() -> new DaedongException(DaedongStatus.PRODUCT_NOT_FOUND));
                if(reviewProductRatingRepository.findByProductAndReview(product, review).isEmpty()) {
                    ReviewProductRating.builder().user(user)
                            .bakery(bakery).product(product).review(review).rating(productRatingRequest.getRating()).build();
                }
            });
        }

        if(request.getNoExistProductRatingRequestList() != null && !request.getNoExistProductRatingRequestList().isEmpty()) {
            request.getNoExistProductRatingRequestList().forEach(noExistProductRatingRequest -> {
                if(productRepository.findByBakeryAndName(bakery, noExistProductRatingRequest.getProductName()).isPresent())
                    throw new DaedongException(DaedongStatus.PRODUCT_DUPLICATE_EXCEPTION);
                Product product = Product.builder().productType(noExistProductRatingRequest.getProductType())
                        .name(noExistProductRatingRequest.getProductName())
                        .price("0").bakery(bakery).isTrue(false).build();
                productRepository.save(product);
                ReviewProductRating.builder().user(user)
                        .bakery(bakery).product(product).review(review).rating(noExistProductRatingRequest.getRating()).build();
            });
        }

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            if (request.getImages().size() > 10) throw new DaedongException(DaedongStatus.IMAGE_NUM_EXCEED_EXCEPTION);
            for (String image : request.getImages()) {
                ReviewImage.builder().review(review).bakery(bakery).image(image).build();
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeReview(String oAuthId, Long reviewId) {
        User user = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Review review = reviewRepository.findByIdAndUser(reviewId, user)
                /*.filter(r -> !r.getIsBlock())*/.orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_NOT_FOUND));
        reviewViewRepository.findByReview(review).ifPresent(reviewViewRepository::delete);
        reviewRepository.delete(review);
//        review.useChange();
    }
}
