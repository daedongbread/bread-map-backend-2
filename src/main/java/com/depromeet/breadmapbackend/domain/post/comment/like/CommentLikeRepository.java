package com.depromeet.breadmapbackend.domain.post.comment.like;

import java.util.Optional;

public interface CommentLikeRepository {
	Optional<CommentLike> findByCommentIdAndUserId(Long commentId, Long userId);

	CommentLike save(CommentLike commentLike);

	void delete(CommentLike commentLike);
}