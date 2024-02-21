package com.depromeet.breadmapbackend.domain.community.like;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * CommunityLikeJpaRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/24
 */
public interface CommunityLikeJpaRepository extends JpaRepository<CommunityLike, Long> {
	Optional<CommunityLike> findByCommunityIdAndUserId(Long communityId, Long userId);

	@Modifying
	@Query("delete from PostLike pl where pl.post.id = :postId")
	void deleteByPostId(@Param("postId") Long postId);
}
