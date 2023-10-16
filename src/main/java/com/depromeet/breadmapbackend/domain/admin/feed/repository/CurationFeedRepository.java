package com.depromeet.breadmapbackend.domain.admin.feed.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.depromeet.breadmapbackend.domain.admin.feed.domain.CurationFeed;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedStatus;

public interface CurationFeedRepository extends JpaRepository<CurationFeed, Long> {

	List<CurationFeed> findByActivatedAndActiveTimeBetween(FeedStatus activated, LocalDateTime startTime,
		LocalDateTime endTime);

	long countByActivatedAndActiveTimeBetween(FeedStatus activated, LocalDateTime startTime,
		LocalDateTime endTime);
}
