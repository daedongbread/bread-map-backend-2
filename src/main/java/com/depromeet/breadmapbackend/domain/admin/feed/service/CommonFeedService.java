package com.depromeet.breadmapbackend.domain.admin.feed.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.admin.dto.FeedLikeResponse;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.CurationFeed;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.Feed;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.FeedAssembler;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.FeedSearchRequest;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.FeedResponseForAdmin;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.FeedResponseForUser;
import com.depromeet.breadmapbackend.domain.admin.feed.repository.FeedRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommonFeedService {

	private final UserRepository userRepository;
	private final FeedRepository repository;

	public FeedResponseForAdmin getAllFeedForAdmin(Pageable pageable, FeedSearchRequest searchRequest) {

		Page<Feed> feeds = repository.findAllFeedBySearch(pageable, searchRequest);

		return FeedAssembler.toDtoForAdmin(feeds.getTotalPages(), feeds.getTotalElements(), feeds.getContent());
	}

	public List<FeedResponseForUser> getAllFeedForUser() {

		List<Feed> feeds = repository.getAllFeedForUser();
		return FeedAssembler.toDtoForUser(feeds);
	}

	@Transactional
	public FeedLikeResponse likeFeed(Long userId, Long feedId) {

		User user = findUserById(userId);
		CurationFeed feed = (CurationFeed)findFeedById(feedId);

		feed.like(user);

		int likeCountByUser = feed.getLikeCountByUser(userId);

		return FeedLikeResponse.builder()
			.userId(userId)
			.likeCounts(likeCountByUser)
			.likeStatus(likeCountByUser > 0 ? "LIKE" : "NONE")
			.build();
	}

	private User findUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
	}

	private Feed findFeedById(Long feedId) {
		return repository.findById(feedId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.FEED_NOT_FOUND));
	}

}
