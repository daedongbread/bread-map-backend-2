package com.depromeet.breadmapbackend.domain.admin.feed.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.admin.feed.domain.Feed;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.FeedAssembler;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.FeedSearchRequest;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.FeedResponseForAdmin;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.FeedResponseForUser;
import com.depromeet.breadmapbackend.domain.admin.feed.repository.FeedRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommonFeedService {

	private final FeedRepository repository;

	public List<FeedResponseForAdmin> getAllFeedForAdmin(Pageable pageable, FeedSearchRequest searchRequest) {

		Page<Feed> feeds = repository.findAllFeedBySearch(pageable, searchRequest);

		return FeedAssembler.toDtoForAdmin(feeds.getContent());
	}

	public List<FeedResponseForUser> getAllFeedForUser() {

		List<Feed> feeds = repository.getAllFeedForUser();
		return FeedAssembler.toDtoForUser(feeds);
	}
}
