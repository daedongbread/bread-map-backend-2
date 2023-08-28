package com.depromeet.breadmapbackend.domain.admin.feed.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.depromeet.breadmapbackend.domain.admin.feed.domain.Feed;

public interface FeedRepository extends JpaRepository<Feed, Long>, FeedQueryRepository {

	@Query("select f from Feed f join fetch f.category where f.id = :feedId")
	Optional<Feed> findByIdFetchCategory(@Param("feedId") Long feedId);
}
