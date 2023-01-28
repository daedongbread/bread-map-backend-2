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
import com.depromeet.breadmapbackend.web.controller.review.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
    public List<ReviewDto> getSimpleBakeryReviewList(Long bakeryId, ReviewSortType sort) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        Comparator<ReviewDto> comparing;
        if(sort == null || sort.equals(ReviewSortType.LATEST)) comparing = Comparator.comparing(ReviewDto::getId).reversed();
        else if(sort.equals(ReviewSortType.HIGH)) comparing = Comparator.comparing(ReviewDto::getAverageRating).reversed();
        else if(sort.equals(ReviewSortType.LOW)) comparing = Comparator.comparing(ReviewDto::getAverageRating);
        else throw new DaedongException(DaedongStatus.BAKERY_SORT_TYPE_EXCEPTION);

        return reviewRepository.findByBakery(bakery)
                .stream().filter(rv -> rv.getStatus().equals(ReviewStatus.UNBLOCK))
                .map(br -> new ReviewDto(br,
                        reviewRepository.countByUser(br.getUser()),
                        followRepository.countByFromUser(br.getUser())
                ))
                .sorted(comparing)
                .limit(3)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<ReviewDto> getBakeryReviewList(Long bakeryId, ReviewSortType sort){ //TODO : 페이징
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        Comparator<ReviewDto> comparing;
        if(sort == null || sort.equals(ReviewSortType.LATEST)) comparing = Comparator.comparing(ReviewDto::getId).reversed();
        else if(sort.equals(ReviewSortType.HIGH)) comparing = Comparator.comparing(ReviewDto::getAverageRating).reversed();
        else if(sort.equals(ReviewSortType.LOW)) comparing = Comparator.comparing(ReviewDto::getAverageRating);
        else throw new DaedongException(DaedongStatus.BAKERY_SORT_TYPE_EXCEPTION);

        return reviewRepository.findByBakery(bakery)
                .stream().filter(rv -> rv.getStatus().equals(ReviewStatus.UNBLOCK))
                .map(br -> new ReviewDto(br,
//                            Math.toIntExact(reviewRepository.countByUserId(br.getUser().getId()))
                        reviewRepository.countByUser(br.getUser()),
                        followRepository.countByFromUser(br.getUser())
                ))
                .sorted(comparing)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReviewDetailDto getReview(Long reviewId) {
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
                .review(review)
//                .reviewNum(Math.toIntExact(reviewRepository.countByUserId(review.getUser().getId())))
                .reviewNum(reviewRepository.countByUser(review.getUser()))
                .followerNum(followRepository.countByToUser(review.getUser()))
                .userOtherReviews(userOtherReviews)
                .bakeryOtherReviews(bakeryOtherReviews)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public ReviewAddDto addReview(String username, Long bakeryId, ReviewRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));

        Review review = Review.builder()
                .user(user).bakery(bakery).content(request.getContent())/*.isUse(true)*/.build();
        reviewRepository.save(review);

        if(request.getProductRatingList() != null) {
            request.getProductRatingList().forEach(productRatingRequest -> {
                Product product = productRepository.findById(productRatingRequest.getProductId()).orElseThrow(() -> new DaedongException(DaedongStatus.PRODUCT_NOT_FOUND));
                if(reviewProductRatingRepository.findByProductAndReview(product, review).isEmpty()) {
                    ReviewProductRating reviewProductRating = ReviewProductRating.builder().bakery(bakery)
                            .product(product).review(review).rating(productRatingRequest.getRating()).build();
                    reviewProductRatingRepository.save(reviewProductRating);
                    review.addRating(reviewProductRating);
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
                ReviewProductRating reviewProductRating = ReviewProductRating.builder().bakery(bakery)
                        .product(product).review(review).rating(noExistProductRatingRequest.getRating()).build();
                reviewProductRatingRepository.save(reviewProductRating);
                review.addRating(reviewProductRating);
            });
        }

        return ReviewAddDto.builder().reviewId(review.getId()).build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void addReviewImage(String username, Long reviewId, List<MultipartFile> files) throws IOException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Review review = reviewRepository.findByIdAndUser(reviewId, user)
                .filter(r -> r.getStatus().equals(ReviewStatus.UNBLOCK)).orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_NOT_FOUND));
        Bakery bakery = review.getBakery();

        if (files.size() > 10) throw new DaedongException(DaedongStatus.IMAGE_NUM_EXCEED_EXCEPTION);
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;
            String imagePath = fileConverter.parseFileInfo(file, ImageType.REVIEW_IMAGE, bakery.getId());
            String image = s3Uploader.upload(file, imagePath);
            ReviewImage reviewImage = ReviewImage.builder()
                    .review(review).bakery(bakery).imageType(ImageType.REVIEW_IMAGE).image(image).build();
            review.addImage(reviewImage);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void addReviewTest(String username, Long bakeryId, ReviewRequest request, List<MultipartFile> files) throws IOException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));

        Review review = Review.builder()
                .user(user).bakery(bakery).content(request.getContent())/*.isUse(true)*/.build();
        reviewRepository.save(review);

        if(request.getProductRatingList() != null) {
            request.getProductRatingList().forEach(productRatingRequest -> {
                Product product = productRepository.findById(productRatingRequest.getProductId()).orElseThrow(() -> new DaedongException(DaedongStatus.PRODUCT_NOT_FOUND));
                if(reviewProductRatingRepository.findByProductAndReview(product, review).isEmpty()) {
                    ReviewProductRating reviewProductRating = ReviewProductRating.builder().bakery(bakery)
                            .product(product).review(review).rating(productRatingRequest.getRating()).build();
                    reviewProductRatingRepository.save(reviewProductRating);
                    review.addRating(reviewProductRating);
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
                ReviewProductRating reviewProductRating = ReviewProductRating.builder().bakery(bakery)
                        .product(product).review(review).rating(noExistProductRatingRequest.getRating()).build();
                reviewProductRatingRepository.save(reviewProductRating);
                review.addRating(reviewProductRating);
            });
        }

        if (files.size() > 10) throw new DaedongException(DaedongStatus.IMAGE_NUM_EXCEED_EXCEPTION);
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;
            String imagePath = fileConverter.parseFileInfo(file, ImageType.REVIEW_IMAGE, bakery.getId());
            String image = s3Uploader.upload(file, imagePath);
            ReviewImage reviewImage = ReviewImage.builder()
                    .review(review).bakery(bakery).imageType(ImageType.REVIEW_IMAGE).image(image).build();
            review.addImage(reviewImage);
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
    public List<ReviewCommentDto> getReviewCommentList(Long reviewId) {
        if(reviewRepository.findById(reviewId)
                .filter(r -> r.getStatus().equals(ReviewStatus.UNBLOCK)).isEmpty()) throw new DaedongException(DaedongStatus.REVIEW_NOT_FOUND);

        return reviewCommentRepository.findByReviewIdAndParentIsNull(reviewId)
                .stream().map(ReviewCommentDto::new).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addReviewComment(String username, Long reviewId, ReviewCommentRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Review review = reviewRepository.findById(reviewId)
                .filter(r -> r.getStatus().equals(ReviewStatus.UNBLOCK)).orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_NOT_FOUND));

        if(request.getParentCommentId().equals(0L)) { // 댓글
            ReviewComment reviewComment = ReviewComment.builder()
                    .review(review).user(user).content(request.getContent()).build();
            reviewCommentRepository.save(reviewComment);
            review.addComment(reviewComment); //TODO
            eventPublisher.publishEvent(ReviewCommentEvent.builder()
                    .userId(review.getUser().getId()).fromUserId(user.getId())
                    .reviewId(reviewId).reviewContent(review.getContent()).build());

        } else { // 대댓글
            ReviewComment parentComment = reviewCommentRepository.findById(request.getParentCommentId()).orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_COMMENT_NOT_FOUND));
            ReviewComment reviewComment = ReviewComment.builder()
                    .review(review).user(user).content(request.getContent()).parent(parentComment).build();
            reviewCommentRepository.save(reviewComment);
            parentComment.addChildComment(reviewComment);
            review.addComment(reviewComment); //TODO
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
        ReviewCommentLike reviewCommentLike = ReviewCommentLike.builder().reviewComment(reviewComment).user(user).build();
        reviewComment.plusLike(reviewCommentLike);
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
