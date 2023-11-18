package com.depromeet.breadmapbackend.domain.post.comment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.depromeet.breadmapbackend.domain.post.PostTopic;
import com.depromeet.breadmapbackend.domain.post.comment.dto.CommentInfo;

/**
 * CommentRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/21
 */
public interface CommentRepository {
	Comment save(Comment comment);

	Page<CommentInfo> findComment(
		Long postId,
		PostTopic postTopic,
		Long userId,
		int page
	);

	Optional<Comment> findByIdAndUserId(Long commentId, Long userId);

	void deleteAllByIdInBatch(List<Long> commentIdList);

	List<Long> findCommentIdListByPostId(Long postId);

	Optional<Comment> findById(Long id);

	Optional<Comment> findByIdAndPostId(Long commentId, Long postId);
}
