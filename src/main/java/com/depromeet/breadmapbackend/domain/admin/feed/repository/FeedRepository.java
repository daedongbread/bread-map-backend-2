package com.depromeet.breadmapbackend.domain.admin.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.depromeet.breadmapbackend.domain.admin.feed.domain.Feed;

public interface FeedRepository extends JpaRepository<Feed, Long>, FeedQueryRepository {

}
