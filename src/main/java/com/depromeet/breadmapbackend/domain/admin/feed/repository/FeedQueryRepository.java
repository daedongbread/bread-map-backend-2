package com.depromeet.breadmapbackend.domain.admin.feed.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.depromeet.breadmapbackend.domain.admin.feed.domain.Feed;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.FeedSearchRequest;

public interface FeedQueryRepository {

	Page<Feed> findAllFeedBySearch(Pageable pageable, FeedSearchRequest feedSearchRequest);

	List<Feed> getAllFeedForUser();
}
