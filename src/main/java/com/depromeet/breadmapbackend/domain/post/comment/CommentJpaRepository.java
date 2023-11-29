package com.depromeet.breadmapbackend.domain.post.comment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.depromeet.breadmapbackend.domain.post.PostTopic;

/**
 * CommentJpaRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/23
 */
public interface CommentJpaRepository extends JpaRepository<Comment, Long> {
	Optional<Comment> findByIdAndUserId(Long commentId, Long userId);

	Optional<Comment> findByIdAndPostTopic(Long commentId, PostTopic postTopic);

	@Modifying
	@Query("delete from Comment c where c.postId = :postId")
	void deleteByPostId(@Param("postId") Long postId);

	@Query("select c.id from Comment c where c.postId = :postId")
	List<Long> findCommentIdListByPostId(@Param("postId") Long postId);

	Optional<Comment> findByIdAndPostIdAndPostTopic(Long commentId, Long postId, PostTopic postTopic);
}
