package com.depromeet.breadmapbackend.service.review;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.exception.DaedongException;
import com.depromeet.breadmapbackend.domain.exception.DaedongStatus;
import com.depromeet.breadmapbackend.domain.product.Product;
import com.depromeet.breadmapbackend.domain.bakery.repository.BakeryRepository;
import com.depromeet.breadmapbackend.domain.product.repository.ProductRepository;
import com.depromeet.breadmapbackend.domain.common.converter.FileConverter;
import com.depromeet.breadmapbackend.domain.common.ImageType;
import com.depromeet.breadmapbackend.domain.review.*;
import com.depromeet.breadmapbackend.domain.review.repository.*;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.repository.FollowRepository;
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.service.S3Uploader;
import com.depromeet.breadmapbackend.web.controller.common.PageResponseDto;
import com.depromeet.breadmapbackend.web.controller.review.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewQueryRepository reviewQueryRepository;
    private final UserRepository userRepository;
    private final BakeryRepository bakeryRepository;
    private final ProductRepository productRepository;
    private final ReviewProductRatingRepository reviewProductRatingRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewCommentRepository reviewCommentRepository;
    private final ReviewCommentLikeRepository reviewCommentLikeRepository;
    private final FollowRepository followRepository;
    private final ReviewReportRepository reviewReportRepository;
    private final FileConverter fileConverter;
    private final S3Uploader s3Uploader;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageResponseDto<ReviewDto> getBakeryReviewList(String username, Long bakeryId, ReviewSortType sortBy, int page) {
        User me = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
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
    public PageResponseDto<ReviewDto> getProductReviewList(String username, Long bakeryId, Long productId, ReviewSortType sortBy, int page) {
        User me = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        Product product = productRepository.findByBakeryAndId(bakery, productId).orElseThrow(() -> new DaedongException(DaedongStatus.PRODUCT_NOT_FOUND));

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
    public PageResponseDto<ReviewDto> getUserReviewList(String username, Long userId, int page) {
        User me = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
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
    public ReviewDetailDto getReview(String username, Long reviewId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Review review = reviewRepository.findById(reviewId)
                .filter(r -> r.getStatus().equals(ReviewStatus.UNBLOCK)).orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_NOT_FOUND));
        review.addViews();

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
    public void addReview(String username, Long bakeryId, ReviewRequest request, List<MultipartFile> files) throws IOException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));

        Review review = Review.builder()
                .user(user).bakery(bakery).content(request.getContent())/*.isUse(true)*/.build();
        reviewRepository.save(review);

        if(request.getProductRatingList() != null) {
            request.getProductRatingList().forEach(productRatingRequest -> {
                Product product = productRepository.findById(productRatingRequest.getProductId()).orElseThrow(() -> new DaedongException(DaedongStatus.PRODUCT_NOT_FOUND));
                if(reviewProductRatingRepository.findByProductAndReview(product, review).isEmpty()) {
                    ReviewProductRating.builder()
                            .bakery(bakery).product(product).review(review).rating(productRatingRequest.getRating()).build();
                }
            });
        }

        if(request.getNoExistProductRatingRequestList() != null) {
            request.getNoExistProductRatingRequestList().forEach(noExistProductRatingRequest -> {
                if(productRepository.findByBakeryAndName(bakery, noExistProductRatingRequest.getProductName()).isPresent())
                    throw new DaedongException(DaedongStatus.PRODUCT_DUPLICATE_EXCEPTION);
                Product product = Product.builder().productType(noExistProductRatingRequest.getProductType())
                        .name(noExistProductRatingRequest.getProductName())
                        .price("0").bakery(bakery).isTrue(false).build();
                productRepository.save(product);
                ReviewProductRating.builder()
                        .bakery(bakery).product(product).review(review).rating(noExistProductRatingRequest.getRating()).build();
            });
        }

        if (files != null) {
            if (files.size() > 10) throw new DaedongException(DaedongStatus.IMAGE_NUM_EXCEED_EXCEPTION);
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;
                String imagePath = fileConverter.parseFileInfo(file, ImageType.REVIEW_IMAGE, bakery.getId());
                String image = s3Uploader.upload(file, imagePath);
                ReviewImage.builder()
                        .review(review).bakery(bakery).imageType(ImageType.REVIEW_IMAGE).image(image).build();
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeReview(String username, Long reviewId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Review review = reviewRepository.findByIdAndUser(reviewId, user)
                /*.filter(r -> r.getStatus().equals(ReviewStatus.UNBLOCK))*/.orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_NOT_FOUND));
        reviewRepository.delete(review);
//        review.useChange();
    }

    @Transactional(rollbackFor = Exception.class)
    public void reviewLike(String username, Long reviewId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Review review = reviewRepository.findById(reviewId)
                .filter(r -> r.getStatus().equals(ReviewStatus.UNBLOCK)).orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_NOT_FOUND));

        if(reviewLikeRepository.findByUserAndReview(user, review).isPresent()) throw new DaedongException(DaedongStatus.REVIEW_LIKE_DUPLICATE_EXCEPTION);

        ReviewLike reviewLike = ReviewLike.builder().review(review).user(user).build();
        review.plusLike(reviewLike);
        eventPublisher.publishEvent(ReviewLikeEvent.builder()
                .userId(review.getUser().getId()).fromUserId(user.getId())
                .reviewId(reviewId).reviewContent(review.getContent()).build());
    }

    @Transactional(rollbackFor = Exception.class)
    public void reviewUnlike(String username, Long reviewId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Review review = reviewRepository.findById(reviewId)
                .filter(r -> r.getStatus().equals(ReviewStatus.UNBLOCK)).orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_NOT_FOUND));

        ReviewLike reviewLike = reviewLikeRepository.findByUserAndReview(user, review)
                .orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_UNLIKE_DUPLICATE_EXCEPTION));
        review.minusLike(reviewLike);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<ReviewCommentDto> getReviewCommentList(Long reviewId) { // TODO : slice
        if(reviewRepository.findById(reviewId).filter(r -> r.getStatus().equals(ReviewStatus.UNBLOCK)).isEmpty())
            throw new DaedongException(DaedongStatus.REVIEW_NOT_FOUND);

        return reviewCommentRepository.findByReviewIdAndParentIsNull(reviewId)
                .stream().map(ReviewCommentDto::new).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addReviewComment(String username, Long reviewId, ReviewCommentRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Review review = reviewRepository.findById(reviewId)
                .filter(r -> r.getStatus().equals(ReviewStatus.UNBLOCK)).orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_NOT_FOUND));

        if(request.getParentCommentId().equals(0L)) { // 댓글
            ReviewComment.builder().review(review).user(user).content(request.getContent()).build();
            eventPublisher.publishEvent(ReviewCommentEvent.builder()
                    .userId(review.getUser().getId()).fromUserId(user.getId())
                    .reviewId(reviewId).reviewContent(review.getContent()).build());

        } else { // 대댓글
            ReviewComment parentComment = reviewCommentRepository.findById(request.getParentCommentId()).orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_COMMENT_NOT_FOUND));
            ReviewComment.builder().review(review).user(user).content(request.getContent()).parent(parentComment).build();
            eventPublisher.publishEvent(RecommentEvent.builder()
                    .userId(parentComment.getUser().getId()).fromUserId(user.getId())
                    .commentId(parentComment.getId()).commentContent(parentComment.getContent()).build());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeReviewComment(String username, Long reviewId, Long commentId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        ReviewComment reviewComment = reviewCommentRepository.findByIdAndUser(commentId, user)
                .orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_COMMENT_NOT_FOUND));
        Review review = reviewRepository.findById(reviewId)
                .filter(r -> r.getStatus().equals(ReviewStatus.UNBLOCK)).orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_NOT_FOUND));

        if(reviewComment.getParent() == null) { // 부모 댓글
            if(reviewComment.getChildList().isEmpty()) { // 자식 댓글이 없으면
                reviewCommentRepository.delete(reviewComment);
            } else { // 자식 댓글이 있으면
                reviewComment.delete();
            }
        } else { // 자식 댓글
            if(!reviewComment.getParent().isDelete()) { // 부모 댓글이 있으면, isDelete = false
                reviewComment.getParent().removeChildComment(reviewComment);
                reviewCommentRepository.delete(reviewComment);
            } else { // 부모 댓글이 없으면, isDelete = true
                if(reviewComment.getParent().getChildList().size() == 1) { // 자식 댓글이 마지막이었으면
                    ReviewComment parent = reviewComment.getParent();
                    parent.removeChildComment(reviewComment);
                    reviewCommentRepository.delete(reviewComment);
                    reviewCommentRepository.delete(parent); // 삭제가 안됨!!
                    review.removeComment(parent);
                } else { // 자식 댓글이 마지막이 아니면
                    reviewComment.getParent().removeChildComment(reviewComment);
                    reviewCommentRepository.delete(reviewComment);
                }
            }
            review.removeComment(reviewComment);
        }
        /*
        1. 부모 댓글이고 자식 댓글이 없으면 삭제
        2. 부모 댓글이고 자식 댓글이 있으면 isDelete = true
        3. 자식 댓글이고 부모 댓글이 있으면 자식 댓글만 삭제
        4. 자식 댓글이고 부모 댓글이 isDelete = true 인데 자식 댓글이 마지막이었으면 부모 댓글까지 삭제
        5. 자식 댓글이고 부모 댓글이 isDelete = true 인데 자식 댓글이 마지막이 아니면 자식 댓글만 삭제
         */
    }


    @Transactional(rollbackFor = Exception.class)
    public void reviewCommentLike(String username, Long reviewId, Long commentId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        if(reviewRepository.findById(reviewId)
                .filter(r -> r.getStatus().equals(ReviewStatus.UNBLOCK)).isEmpty()) throw new DaedongException(DaedongStatus.REVIEW_NOT_FOUND);
        ReviewComment reviewComment = reviewCommentRepository.findByIdAndUser(commentId, user).orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_COMMENT_NOT_FOUND));

        if(reviewCommentLikeRepository.findByUserAndReviewComment(user, reviewComment).isPresent())
            throw new DaedongException(DaedongStatus.REVIEW_COMMENT_LIKE_DUPLICATE_EXCEPTION);
        ReviewCommentLike.builder().reviewComment(reviewComment).user(user).build();

        eventPublisher.publishEvent(ReviewCommentLikeEvent.builder()
                .userId(reviewComment.getUser().getId()).fromUserId(user.getId())
                .commentId(reviewComment.getId()).commentContent(reviewComment.getContent()).build());
    }

    @Transactional(rollbackFor = Exception.class)
    public void reviewCommentUnlike(String username, Long reviewId, Long commentId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        if(reviewRepository.findById(reviewId)
                .filter(r -> r.getStatus().equals(ReviewStatus.UNBLOCK)).isEmpty()) throw new DaedongException(DaedongStatus.REVIEW_NOT_FOUND);
        ReviewComment reviewComment = reviewCommentRepository.findByIdAndUser(commentId, user).orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_COMMENT_NOT_FOUND));

        ReviewCommentLike reviewCommentLike = reviewCommentLikeRepository.findByUserAndReviewComment(user, reviewComment)
                .orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_COMMENT_UNLIKE_DUPLICATE_EXCEPTION));
        reviewComment.minusLike(reviewCommentLike);
        reviewCommentLikeRepository.delete(reviewCommentLike);
    }

    @Transactional(rollbackFor = Exception.class)
    public void reviewReport(String username, Long reviewId, ReviewReportRequest request) {
        User reporter = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_NOT_FOUND));

        ReviewReport reviewReport = ReviewReport.builder()
                .reporter(reporter).review(review).reason(request.getReason()).content(request.getContent()).build();

        reviewReportRepository.save(reviewReport);
    }
}
