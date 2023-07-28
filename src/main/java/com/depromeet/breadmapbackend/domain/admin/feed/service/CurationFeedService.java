package com.depromeet.breadmapbackend.domain.admin.feed.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.admin.Admin;
import com.depromeet.breadmapbackend.domain.admin.AdminRepository;
import com.depromeet.breadmapbackend.domain.admin.category.domain.Category;
import com.depromeet.breadmapbackend.domain.admin.category.repository.CategoryRepository;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.CurationFeed;
import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedType;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.FeedAssembler;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.request.FeedRequestDto;
import com.depromeet.breadmapbackend.domain.admin.feed.dto.response.FeedResponseDto;
import com.depromeet.breadmapbackend.domain.admin.feed.repository.CurationFeedRepository;
import com.depromeet.breadmapbackend.domain.admin.feed.repository.FeedRepository;
import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryRepository;
import com.depromeet.breadmapbackend.domain.bakery.product.Product;
import com.depromeet.breadmapbackend.domain.bakery.product.ProductRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CurationFeedService implements FeedService {

	private final FeedRepository feedRepository;
	private final CurationFeedRepository repository;
	private final BakeryRepository bakeryRepository;
	private final AdminRepository adminRepository;
	private final CategoryRepository categoryRepository;
	private final ProductRepository productRepository;

	@Transactional
	@Override
	public Long addFeed(Long adminId, FeedRequestDto requestDto) {

		Admin admin = findAdminById(adminId);
		Category category = findCategoryById(requestDto.getCommon().getCategoryId());

		CurationFeed feed = (CurationFeed)FeedAssembler.toEntity(admin, category, requestDto);

		List<Bakery> bakeries = bakeryRepository.findBakeriesInIds(requestDto.getBakeryIds());

		feed.addAll(bakeries, requestDto);

		CurationFeed newFeed = repository.save(feed);

		return newFeed.getId();
	}

	@Transactional
	@Override
	public void updateFeed(Long feedId, FeedRequestDto updateDto) {

		CurationFeed feed = findCurationFeedById(feedId);
		Category category = findCategoryById(updateDto.getCommon().getCategoryId());

		List<Bakery> bakeries = bakeryRepository.findBakeriesInIds(updateDto.getBakeryIds());

		feed.update(bakeries, category, updateDto);
	}

	@Override
	public FeedResponseDto getFeed(Long feedId) {

		CurationFeed curationFeed = findCurationFeedFetchCategory(feedId);

		List<Product> products = productRepository.findByIdIn(curationFeed.getBakeries().getProductIdList());
		List<Bakery> bakeries = products.stream().map(Product::getBakery).collect(Collectors.toList());

		return FeedResponseDto.builder()
			.common(FeedAssembler.toCommonDto(curationFeed))
			.curation(FeedAssembler.toCurationDto(bakeries, products))
			.likeCounts(curationFeed.getLikeCount())
			.build();
	}

	@Override
	public FeedType getServiceType() {
		return FeedType.CURATION;
	}

	private CurationFeed findCurationFeedById(Long feedId) {
		return repository.findById(feedId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.FEED_NOT_FOUND));
	}

	private CurationFeed findCurationFeedFetchCategory(Long feedId) {
		return (CurationFeed)feedRepository.findByIdFetchCategory(feedId)
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
