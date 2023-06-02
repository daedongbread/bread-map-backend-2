package com.depromeet.breadmapbackend.domain.review.like;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.notice.NoticableEvent;
import com.depromeet.breadmapbackend.domain.notice.NoticeType;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewLikeServiceImpl implements ReviewLikeService {
	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;
	private final ReviewLikeRepository reviewLikeRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional(rollbackFor = Exception.class)
	public void reviewLike(String oAuthId, Long reviewId) {
		User user = userRepository.findByOAuthId(oAuthId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		Review review = reviewRepository.findByIdAndIsBlockIsFalseAndIsDeleteIsFalse(reviewId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_NOT_FOUND));

		if (reviewLikeRepository.findByUserAndReview(user, review).isPresent())
			throw new DaedongException(DaedongStatus.REVIEW_LIKE_DUPLICATE_EXCEPTION);

		reviewLikeRepository.save(ReviewLike.builder().review(review).user(user).build());

		eventPublisher.publishEvent(
			NoticableEvent.builder()
				.isAlarmOn(user.getIsAlarmOn())
				.userId(review.getUser().getId())
				.fromUserId(user.getId())
				.contentId(review.getId())
				.content(review.getContent())
				.noticeType(NoticeType.REVIEW_LIKE)
				.build()
		);
	}

	@Transactional(rollbackFor = Exception.class)
	public void reviewUnlike(String oAuthId, Long reviewId) {
		User user = userRepository.findByOAuthId(oAuthId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		Review review = reviewRepository.findByIdAndIsBlockIsFalseAndIsDeleteIsFalse(reviewId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_NOT_FOUND));

		reviewLikeRepository.delete(reviewLikeRepository.findByUserAndReview(user, review)
			.orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_UNLIKE_DUPLICATE_EXCEPTION)));
	}
}
