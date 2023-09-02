package com.depromeet.breadmapbackend.domain.admin.feed.service;

import com.depromeet.breadmapbackend.domain.admin.feed.domain.Feed;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.LandingFeedResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.admin.Admin;
import com.depromeet.breadmapbackend.domain.admin.AdminRepository;
import com.depromeet.breadmapbackend.domain.admin.category.domain.Category;
import com.depromeet.breadmapbackend.domain.admin.category.repository.CategoryRepository;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedType;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.LandingFeed;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.FeedAssembler;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.FeedRequestDto;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.FeedResponseDto;
import com.depromeet.breadmapbackend.domain.admin.feed.repository.LandingFeedRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LandingFeedService implements FeedService {

	private final LandingFeedRepository repository;
	private final AdminRepository adminRepository;
	private final CategoryRepository categoryRepository;

	@Transactional
	@Override
	public Long addFeed(Long adminId, FeedRequestDto requestDto) {

		Admin admin = findAdminById(adminId);
		Category category = findCategoryById(requestDto.getCommon().getCategoryId());

		LandingFeed feed = (LandingFeed)FeedAssembler.toEntity(admin, category, requestDto);

		LandingFeed newFeed = repository.save(feed);

		return newFeed.getId();
	}

	@Transactional
	@Override
	public void updateFeed(Long adminId, Long feedId, FeedRequestDto updateDto) {
		Admin admin = findAdminById(adminId);
		LandingFeed feed = findLandingFeedById(feedId);
		Category category = findCategoryById(updateDto.getCommon().getCategoryId());

		Feed updateFeed = FeedAssembler.toEntity(admin, category, updateDto);

		feed.update(updateFeed);
	}

	@Override
	public FeedResponseDto getFeed(Long feedId) {

		LandingFeed landingFeed = findLandingFeedById(feedId);

		return FeedResponseDto.builder()
			.common(FeedAssembler.toCommonDto(landingFeed))
			.landing(FeedAssembler.toLandingDto(landingFeed))
			.build();
	}

	@Override
	public FeedType getServiceType() {
		return FeedType.LANDING;
	}

	private LandingFeed findLandingFeedById(Long feedId) {
		return repository.findById(feedId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.FEED_NOT_FOUND));
	}

	private Admin findAdminById(Long adminId) {
		return adminRepository.findById(adminId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.ADMIN_NOT_FOUND));
	}

	private Category findCategoryById(Long categoryId) {
		return categoryRepository.findById(categoryId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.CATEGORY_NOT_FOUND));
	}
}
