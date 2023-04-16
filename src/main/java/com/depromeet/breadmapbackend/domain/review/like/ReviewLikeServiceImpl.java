package com.depromeet.breadmapbackend.domain.review.like;

import com.depromeet.breadmapbackend.domain.bakery.BakeryRepository;
import com.depromeet.breadmapbackend.domain.bakery.product.ProductRepository;
import com.depromeet.breadmapbackend.domain.review.*;
import com.depromeet.breadmapbackend.domain.review.comment.ReviewCommentRepository;
import com.depromeet.breadmapbackend.domain.review.comment.like.ReviewCommentLikeRepository;
import com.depromeet.breadmapbackend.domain.review.report.ReviewReportRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.domain.user.follow.FollowRepository;
import com.depromeet.breadmapbackend.global.S3Uploader;
import com.depromeet.breadmapbackend.global.converter.FileConverter;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewLikeServiceImpl implements ReviewLikeService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(rollbackFor = Exception.class)
    public void reviewLike(String oAuthId, Long reviewId) {
        User user = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Review review = reviewRepository.findById(reviewId)
                .filter(r -> !r.getIsBlock()).orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_NOT_FOUND));

        if(reviewLikeRepository.findByUserAndReview(user, review).isPresent()) throw new DaedongException(DaedongStatus.REVIEW_LIKE_DUPLICATE_EXCEPTION);

        ReviewLike reviewLike = ReviewLike.builder().review(review).user(user).build();
        review.plusLike(reviewLike);
        eventPublisher.publishEvent(ReviewLikeEvent.builder()
                .userId(review.getUser().getId()).fromUserId(user.getId())
                .reviewId(reviewId).reviewContent(review.getContent()).build());
    }

    @Transactional(rollbackFor = Exception.class)
    public void reviewUnlike(String oAuthId, Long reviewId) {
        User user = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Review review = reviewRepository.findById(reviewId)
                .filter(r -> !r.getIsBlock()).orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_NOT_FOUND));

        ReviewLike reviewLike = reviewLikeRepository.findByUserAndReview(user, review)
                .orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_UNLIKE_DUPLICATE_EXCEPTION));
        review.minusLike(reviewLike);
    }
}
