package com.depromeet.breadmapbackend.domain.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.depromeet.breadmapbackend.domain.community.domain.Community;

/**
 * CommunityRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 12/20/23
 */
public interface CommunityRepository extends JpaRepository<Community, Long> {
}
