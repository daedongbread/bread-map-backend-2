package com.depromeet.breadmapbackend.domain.admin.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.depromeet.breadmapbackend.domain.admin.feed.domain.CurationFeed;

public interface CurationFeedRepository extends JpaRepository<CurationFeed, Long> {
}
