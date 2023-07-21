package com.depromeet.breadmapbackend.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * PostJpaRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/20
 */
public interface PostJpaRepository extends JpaRepository<Post, Long> {

}
