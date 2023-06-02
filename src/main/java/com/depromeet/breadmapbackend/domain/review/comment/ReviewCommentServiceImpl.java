package com.depromeet.breadmapbackend.domain.review.comment;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.notice.NoticableEvent;
import com.depromeet.breadmapbackend.domain.notice.NoticeType;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewRepository;
import com.depromeet.breadmapbackend.domain.review.comment.dto.ReviewCommentDto;
import com.depromeet.breadmapbackend.domain.review.comment.dto.ReviewCommentRequest;
import com.depromeet.breadmapbackend.domain.review.comment.like.ReviewCommentLike;
import com.depromeet.breadmapbackend.domain.review.comment.like.ReviewCommentLikeRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewCommentServiceImpl implements ReviewCommentService {
	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;
	private final ReviewCommentRepository reviewCommentRepository;
	private final ReviewCommentLikeRepository reviewCommentLikeRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public List<ReviewCommentDto> getReviewCommentList(Long reviewId) { // TODO : slice
		if (reviewRepository.findByIdAndIsBlockIsFalseAndIsDeleteIsFalse(reviewId).isEmpty())
			throw new DaedongException(DaedongStatus.REVIEW_NOT_FOUND);

		return reviewCommentRepository.findByReviewIdAndParentIsNull(reviewId)
			.stream().map(ReviewCommentDto::new).collect(Collectors.toList());
	}

	@Transactional(rollbackFor = Exception.class)
	public void addReviewComment(String oAuthId, Long reviewId, ReviewCommentRequest request) {
		User user = userRepository.findByOAuthId(oAuthId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		Review review = reviewRepository.findByIdAndIsBlockIsFalseAndIsDeleteIsFalse(reviewId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_NOT_FOUND));

		if (request.getParentCommentId().equals(0L)) { // 댓글
			ReviewComment.builder()
				.review(review)
				.user(user)
				.content(request.getContent())
				.build();

			eventPublisher.publishEvent(
				NoticableEvent.builder()
					.isAlarmOn(user.getIsAlarmOn())
					.userId(review.getUser().getId())
					.fromUserId(user.getId())
					.contentId(review.getId())
					.content(review.getContent())
					.noticeType(NoticeType.REVIEW_COMMENT)
					.build()
			);

		} else { // 대댓글
			ReviewComment parentComment = reviewCommentRepository.findById(request.getParentCommentId())
				.orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_COMMENT_NOT_FOUND));
			ReviewComment.builder()
				.review(review)
				.user(user)
				.content(request.getContent())
				.parent(parentComment)
				.build();

			eventPublisher.publishEvent(
				NoticableEvent.builder()
					.isAlarmOn(user.getIsAlarmOn())
					.userId(parentComment.getUser().getId())
					.fromUserId(user.getId())
					.contentId(parentComment.getId())
					.content(parentComment.getContent())
					.noticeType(NoticeType.REVIEW_COMMENT)
					.build()
			);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void removeReviewComment(String oAuthId, Long reviewId, Long commentId) {
		User user = userRepository.findByOAuthId(oAuthId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		Review review = reviewRepository.findByIdAndIsBlockIsFalseAndIsDeleteIsFalse(reviewId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_NOT_FOUND));
		ReviewComment reviewComment = reviewCommentRepository.findById(commentId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_COMMENT_NOT_FOUND));
		if (!reviewComment.getUser().equals(user) && !reviewComment.getReview().getUser().equals(user))
			throw new DaedongException(DaedongStatus.REVIEW_COMMENT_UNDELETE_EXCEPTION);

		if (reviewComment.getParent() == null) { // 부모 댓글
			if (reviewComment.getChildList().isEmpty()) { // 자식 댓글이 없으면
				reviewCommentRepository.delete(reviewComment);
			} else { // 자식 댓글이 있으면
				reviewComment.delete();
			}
		} else { // 자식 댓글
			if (!reviewComment.getParent().isDelete()) { // 부모 댓글이 있으면, isDelete = false
				reviewComment.getParent().removeChildComment(reviewComment);
				reviewCommentRepository.delete(reviewComment);
			} else { // 부모 댓글이 없으면, isDelete = true
				if (reviewComment.getParent().getChildList().size() == 1) { // 자식 댓글이 마지막이었으면
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
	public void reviewCommentLike(String oAuthId, Long reviewId, Long commentId) {
		User user = userRepository.findByOAuthId(oAuthId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		if (reviewRepository.findByIdAndIsBlockIsFalseAndIsDeleteIsFalse(reviewId).isEmpty())
			throw new DaedongException(DaedongStatus.REVIEW_NOT_FOUND);
		ReviewComment reviewComment = reviewCommentRepository.findById(commentId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_COMMENT_NOT_FOUND));

		if (reviewCommentLikeRepository.findByUserAndReviewComment(user, reviewComment).isPresent())
			throw new DaedongException(DaedongStatus.REVIEW_COMMENT_LIKE_DUPLICATE_EXCEPTION);
		ReviewCommentLike.builder().reviewComment(reviewComment).user(user).build();

		eventPublisher.publishEvent(
			NoticableEvent.builder()
				.isAlarmOn(user.getIsAlarmOn())
				.userId(reviewComment.getUser().getId())
				.fromUserId(user.getId())
				.contentId(reviewComment.getId())
				.content(reviewComment.getContent())
				.noticeType(NoticeType.REVIEW_COMMENT_LIKE)
				.build()
		);
	}

	@Transactional(rollbackFor = Exception.class)
	public void reviewCommentUnlike(String oAuthId, Long reviewId, Long commentId) {
		User user = userRepository.findByOAuthId(oAuthId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		if (reviewRepository.findByIdAndIsBlockIsFalseAndIsDeleteIsFalse(reviewId).isEmpty())
			throw new DaedongException(DaedongStatus.REVIEW_NOT_FOUND);
		ReviewComment reviewComment = reviewCommentRepository.findById(commentId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_COMMENT_NOT_FOUND));

		ReviewCommentLike reviewCommentLike = reviewCommentLikeRepository.findByUserAndReviewComment(user,
				reviewComment)
			.orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_COMMENT_UNLIKE_DUPLICATE_EXCEPTION));
		reviewComment.minusLike(reviewCommentLike);
		reviewCommentLikeRepository.delete(reviewCommentLike);
	}
}
