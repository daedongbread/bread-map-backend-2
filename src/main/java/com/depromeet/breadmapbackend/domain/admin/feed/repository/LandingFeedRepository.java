package com.depromeet.breadmapbackend.domain.admin.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.depromeet.breadmapbackend.domain.admin.feed.domain.LandingFeed;

public interface LandingFeedRepository extends JpaRepository<LandingFeed, Long> {
}
