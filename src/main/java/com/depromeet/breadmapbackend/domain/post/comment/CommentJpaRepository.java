package com.depromeet.breadmapbackend.domain.post.comment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.depromeet.breadmapbackend.domain.post.Post;

/**
 * CommentJpaRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/23
 */
public interface CommentJpaRepository extends JpaRepository<Comment, Long> {
	Optional<Comment> findByIdAndUserId(Long commentId, Long userId);

	@Modifying
	@Query("delete from Comment c where c.post = :post")
	void deleteByPostId(@Param("post") Post post);

	@Query("select c.id from Comment c where c.post = :post")
	List<Long> findCommentIdListByPost(@Param("post") Post post);

	Optional<Comment> findByIdAndPostId(Long commentId, Long postId);
}
