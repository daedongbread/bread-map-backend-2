package com.depromeet.breadmapbackend.domain.post.comment.like;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository {
	Optional<CommentLike> findByCommentIdAndUserId(Long commentId, Long userId);

	CommentLike save(CommentLike commentLike);

	void delete(CommentLike commentLike);

	List<CommentLike> findByPostId(Long postId);

	void deleteAllByCommentIdList(List<Long> commentIdListToDelete);
}