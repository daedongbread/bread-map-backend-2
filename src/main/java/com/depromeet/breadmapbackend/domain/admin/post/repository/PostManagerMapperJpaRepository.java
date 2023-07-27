package com.depromeet.breadmapbackend.domain.admin.post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.depromeet.breadmapbackend.domain.admin.post.domain.PostManagerMapper;

/**
 * PostManagerMapperJpaRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/24
 */
public interface PostManagerMapperJpaRepository extends JpaRepository<PostManagerMapper, Long> {

	@Query(value = "select pmm "
		+ "from PostManagerMapper pmm "
		+ "join fetch pmm.post p "
		+ "where pmm.post.postTopic = 'EVENT'"
		+ "order by p.createdAt desc"
		, countQuery = "select count(pmm) "
		+ "from PostManagerMapper pmm")
	Page<PostManagerMapper> findPostManagerMappers(Pageable page);

	@Query("select count(pmm) "
		+ "from PostManagerMapper pmm "
		+ "where pmm.post.postTopic = 'EVENT' "
		+ "and pmm.isFixed = true "
	)
	long getFixedEventCount();

	@Query("select pmm "
		+ "from PostManagerMapper pmm "
		+ "where pmm.isFixed = true ")
	Optional<PostManagerMapper> findFixedPost();

	@Query("select pm "
		+ "from PostManagerMapper pm "
		+ "join fetch pm.post p "
		+ "where pm.isCarousel = true "
		+ "and pm.isPosted = true "
		+ "and p.postTopic = 'EVENT' "
		+ "order by pm.carouselOrder asc")
	List<PostManagerMapper> findCarouselPosts();
}
