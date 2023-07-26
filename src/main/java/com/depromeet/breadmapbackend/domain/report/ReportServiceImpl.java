package com.depromeet.breadmapbackend.domain.report;

import org.springframework.stereotype.Service;

import com.depromeet.breadmapbackend.domain.post.Post;
import com.depromeet.breadmapbackend.domain.post.PostRepository;
import com.depromeet.breadmapbackend.domain.post.comment.Comment;
import com.depromeet.breadmapbackend.domain.post.comment.CommentRepository;
import com.depromeet.breadmapbackend.domain.report.dto.ReportCommand;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.review.ReviewRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.RequiredArgsConstructor;

/**
 * ReportServiceImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/25
 */
@RequiredArgsConstructor
@Service
public class ReportServiceImpl implements ReportService {

	private final ReportRepository reportRepository;
	private final PostRepository postRepository;
	private final ReviewRepository reviewRepository;
	private final CommentRepository commentRepository;

	@Override
	public void report(final ReportCommand command, final Long userId) {
		final Report report = Report.builder()
			.reporterId(userId)
			.postId(getPostIdBy(command, userId))
			.reportReason(command.reason())
			.content(command.content())
			.reportType(command.reportType())
			.build();

		reportRepository.save(report);
	}

	private Long getPostIdBy(final ReportCommand command, final Long reporterId) {
		return switch (command.reportType()) {
			case REVIEW -> {
				final Review review = reviewRepository.findById(command.contentId())
					.orElseThrow(() -> new DaedongException(DaedongStatus.REVIEW_NOT_FOUND));
				validateUser(review.getUser().getId(), reporterId);
				yield review.getId();
			}

			case BREAD_STORY, FREE_TALK -> {
				final Post post = postRepository.findByPostIdAndPostTopic(command.contentId(),
						command.reportType().getReportType())
					.orElseThrow(() -> new DaedongException(DaedongStatus.POST_NOT_FOUND));
				validateUser(post.getUser().getId(), reporterId);
				yield post.getId();
			}

			case COMMENT -> {
				final Comment comment = commentRepository.findById(command.contentId())
					.orElseThrow(() -> new DaedongException(DaedongStatus.COMMENT_NOT_FOUND));
				validateUser(comment.getUser().getId(), reporterId);
				yield comment.getId();
			}
			default -> throw new DaedongException(DaedongStatus.INVALID_REPORT_TARGET);
		};
	}

	private void validateUser(final Long postUserId, final Long reporterId) {
		if (postUserId.equals(reporterId))
			throw new DaedongException(DaedongStatus.CANNOT_REPORT_OWN_POST);
	}
}
