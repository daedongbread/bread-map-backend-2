package com.depromeet.breadmapbackend.domain.post.comment;

import java.util.Objects;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.notice.dto.NoticeEventDto;
import com.depromeet.breadmapbackend.domain.notice.factory.NoticeType;
import com.depromeet.breadmapbackend.domain.post.Post;
import com.depromeet.breadmapbackend.domain.post.PostRepository;
import com.depromeet.breadmapbackend.domain.post.PostTopic;
import com.depromeet.breadmapbackend.domain.post.comment.dto.Command;
import com.depromeet.breadmapbackend.domain.post.comment.dto.CommentInfo;
import com.depromeet.breadmapbackend.domain.post.comment.dto.UpdateCommand;
import com.depromeet.breadmapbackend.domain.post.comment.like.CommentLike;
import com.depromeet.breadmapbackend.domain.post.comment.like.CommentLikeRepository;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewRepository;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.RequiredArgsConstructor;

/**
 * CommentServiceImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/21
 */
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

	private final CommentRepository commentRepository;
	private final UserRepository userRepository;
	private final CommentLikeRepository commentLikeRepository;
	private final PostRepository postRepository;
	private final ReviewRepository reviewRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	@Override
	public Comment register(final Command command, final Long userId) {
		validateCommentCommand(command);
		final Comment comment = command.toEntity(
			userRepository.findById(userId)
				.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND))
		);
		final Comment savedComment = commentRepository.save(comment);

		final boolean userAuthorOfPostAndReview = isUserAuthorOfPostAndReview(command, userId);

		if (userAuthorOfPostAndReview && command.isFirstDepth())
			return savedComment;

		if (isCaseOfUserReceiveTwoNotices(command)) {
			publishEvent(
				userId,
				savedComment.getId(),
				command.parentId(),
				command.postTopic() == PostTopic.REVIEW
					? NoticeType.REVIEW_RECOMMENT
					: NoticeType.RECOMMENT
			);
			return savedComment;
		}

		if (command.isFirstDepth()) {
			publishEvent(
				userId,
				savedComment.getId(),
				command.postId(),
				command.postTopic() == PostTopic.REVIEW
					? NoticeType.REVIEW_COMMENT
					: NoticeType.COMMUNITY_COMMENT
			);
		} else {
			publishEvent(
				userId,
				savedComment.getId(),
				command.parentId(),
				command.postTopic() == PostTopic.REVIEW
					? NoticeType.REVIEW_RECOMMENT
					: NoticeType.RECOMMENT
			);
		}
		return savedComment;
	}

	private void publishEvent(Long userId, Long contentId, Long subContentId, NoticeType noticeType) {
		eventPublisher.publishEvent(
			NoticeEventDto.builder()
				.userId(userId)
				.contentId(contentId)
				.subContentId(subContentId)
				.noticeType(noticeType)
				.build()
		);
	}

	private boolean isCaseOfUserReceiveTwoNotices(
		final Command command
	) {
		if (command.isFirstDepth())
			return false;

		final Comment parentComment = commentRepository.findById(command.parentId())
			.orElseThrow(() -> new DaedongException(DaedongStatus.COMMENT_NOT_FOUND));

		if (command.postTopic() == PostTopic.REVIEW) {
			final Review review = reviewRepository.findById(command.postId())
				.orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_NOT_FOUND));
			return parentComment.getUser().getId().equals(review.getUser().getId());
		} else {
			final Post post = postRepository.findById(command.postId())
				.orElseThrow(() -> new DaedongException(DaedongStatus.POST_NOT_FOUND));

			return parentComment.getUser().getId().equals(post.getUser().getId());
		}
	}

	private boolean isUserAuthorOfPostAndReview(final Command command, final Long userId) {
		if (command.postTopic() == PostTopic.REVIEW) {

			final Review review = reviewRepository.findById(command.postId())
				.orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_NOT_FOUND));
			return review.getUser().getId().equals(userId);
		} else {
			final Post post = postRepository.findById(command.postId())
				.orElseThrow(() -> new DaedongException(DaedongStatus.POST_NOT_FOUND));
			return post.getUser().getId().equals(userId);
		}
	}

	@Override
	public Page<CommentInfo.Response> findComment(final Long postId, final PostTopic postTopic, final Long userId,
		final int page) {
		return commentRepository.findComment(postId, postTopic, userId, page)
			.map(info -> {
				final CommentResponseStatus status = getStatus(info.isBlocked(), info.status());

				return new CommentInfo.Response(
					info.id(),
					getContentToResponse(info, status),
					info.isFirstDepth(),
					info.parentId(),
					info.targetCommentUserNickname(),
					info.userId(),
					info.nickname(),
					info.profileImage(),
					info.likeCount(),
					info.isUserLiked(),
					info.createdDate(),
					status
				);
			});
	}

	@Override
	@Transactional
	public void updateComment(final UpdateCommand command, final Long userId) {
		commentRepository.findByIdAndUserId(command.commentId(), userId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.COMMENT_NOT_FOUND))
			.update(command.content());
	}

	@Transactional
	@Override
	public void deleteComment(final Long commentId, final Long userId) {
		commentRepository.findByIdAndUserId(commentId, userId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.COMMENT_NOT_FOUND))
			.delete();
	}

	@Transactional
	@Override
	public int toggleLike(final Long commentId, final Long userId) {
		final Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.COMMENT_NOT_FOUND));

		final Optional<CommentLike> commentLike = commentLikeRepository.findByCommentIdAndUserId(commentId, userId);
		if (commentLike.isEmpty()) {
			commentLikeRepository.save(new CommentLike(comment, userId));
			if (!Objects.equals(comment.getUser().getId(), userId))
				eventPublisher.publishEvent(
					NoticeEventDto.builder()
						.userId(userId)
						.contentId(comment.getId())
						.subContentId(comment.getPostId())
						.noticeType(comment.getPostTopic() == PostTopic.REVIEW
							? NoticeType.REVIEW_COMMENT_LIKE
							: NoticeType.COMMENT_LIKE
						)
						.build()
				);
			return 1;
		} else {
			commentLikeRepository.delete(commentLike.get());
			return 0;
		}
	}

	private CommentResponseStatus getStatus(final boolean blocked, final CommentStatus status) {
		return blocked ? CommentResponseStatus.BLOCKED_BY_USER : status.getResponseStatus();
	}

	private String getContentToResponse(final CommentInfo info, final CommentResponseStatus status) {
		if (status != CommentResponseStatus.ACTIVE) {
			return status.replaceContent(info.content());
		}
		return info.content();
	}

	private void validateCommentCommand(final Command command) {
		if (!command.isFirstDepth()) {
			if (command.parentId() == 0)
				throw new DaedongException(DaedongStatus.SECOND_DEPTH_COMMENT_SHOULD_HAVE_PARENT_ID);
			if (command.targetCommentUserId() == 0)
				throw new DaedongException(DaedongStatus.SECOND_DEPTH_COMMENT_SHOULD_HAVE_TARGET_USER_ID);

			commentRepository.findByIdAndPostIdAndPostTopic(command.parentId(), command.postId(), command.postTopic())
				.orElseThrow(() -> new DaedongException(DaedongStatus.COMMENT_NOT_FOUND));

			userRepository.findById(command.targetCommentUserId())
				.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		}
	}
}
