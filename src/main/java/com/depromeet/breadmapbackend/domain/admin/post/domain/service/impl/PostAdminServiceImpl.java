package com.depromeet.breadmapbackend.domain.admin.post.domain.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.admin.carousel.domain.CarouselManager;
import com.depromeet.breadmapbackend.domain.admin.carousel.domain.CarouselType;
import com.depromeet.breadmapbackend.domain.admin.carousel.domain.dto.command.CreateCarouselCommand;
import com.depromeet.breadmapbackend.domain.admin.carousel.domain.service.CarouselManagerService;
import com.depromeet.breadmapbackend.domain.admin.carousel.repository.CarouselRepository;
import com.depromeet.breadmapbackend.domain.admin.post.controller.dto.response.EventResponse;
import com.depromeet.breadmapbackend.domain.admin.post.domain.PostManagerMapper;
import com.depromeet.breadmapbackend.domain.admin.post.domain.dto.command.EventCommand;
import com.depromeet.breadmapbackend.domain.admin.post.domain.dto.info.PostManagerMapperInfo;
import com.depromeet.breadmapbackend.domain.admin.post.domain.repository.PostAdminRepository;
import com.depromeet.breadmapbackend.domain.admin.post.domain.service.PostAdminService;
import com.depromeet.breadmapbackend.domain.notice.dto.NoticeEventDto;
import com.depromeet.breadmapbackend.domain.notice.factory.NoticeType;
import com.depromeet.breadmapbackend.domain.post.Post;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.RequiredArgsConstructor;

/**
 * PostAdminServiceImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/24
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PostAdminServiceImpl implements PostAdminService {

	private static final int MAX_EVENT_CAROUSEL_COUNT = 20;

	@Value("${admin.event.post.user-id}")
	private Long adminUserId;
	private final PostAdminRepository postAdminRepository;
	private final UserRepository userRepository;
	private final CarouselManagerService carouselManagerService;
	private final CarouselRepository carouselRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Override
	public Page<PostManagerMapperInfo> getEventPosts(final int page) {
		Page<PostManagerMapper> postManagerMappers = postAdminRepository.findPostManagerMappers(page);

		List<Long> postIdList = postManagerMappers.map(pmm -> pmm.getPost().getId()).toList();
		List<CarouselManager> carouselManagers = carouselRepository.findByTargetIdIn(postIdList);

		return postManagerMappers.map(pmm ->
			new PostManagerMapperInfo(
				pmm,
				carouselManagers.stream()
					.filter(cm -> cm.getTargetId().equals(pmm.getPost().getId())).findFirst()
					.map(CarouselManager::isCarouseled).orElse(false)
			)
		);
	}

	@Transactional
	@Override
	public PostManagerMapper createEventPost(final EventCommand command) {
		validateEventStatus(command);

		final User adminUser = userRepository.findById(adminUserId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		if (command.isFixed()) {
			postAdminRepository.findFixedPost().ifPresent(PostManagerMapper::unFix);
		}

		final Post savePost = command.toEventPost(adminUser);

		final PostManagerMapper postManagerMapper = PostManagerMapper.builder()
			.post(command.images() != null ? savePost.addImages(command.images()) : savePost)
			.isPosted(command.isPosted())
			.isFixed(command.isFixed())
			.build();

		final PostManagerMapper savedPostManagerMapper =
			postAdminRepository.savePostManagerMapper(postManagerMapper);

		carouselManagerService.saveCarousel(
			new CreateCarouselCommand(
				CarouselType.EVENT,
				savedPostManagerMapper.getId(),
				command.bannerImage(),
				command.isCarousel()
			)
		);
		if (command.isPosted()) {
			eventPublisher.publishEvent(
				NoticeEventDto.builder()
					.contentId(savedPostManagerMapper.getId())
					.noticeType(NoticeType.EVENT)
					.build()
			);
		}

		return savedPostManagerMapper;
	}

	@Override
	public boolean canFixEvent() {
		return postAdminRepository.canFixEvent();
	}

	@Override
	@Transactional
	public void updateEventPost(final EventCommand command, final Long managerId) {
		validateEventStatus(command);

		final PostManagerMapper postManagerMapper = postAdminRepository.findPostManagerMapperById(managerId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.POST_NOT_FOUND));
		final boolean beforePostStatus = postManagerMapper.isPosted();

		final CarouselManager carouselManager =
			carouselRepository.findByTargetIdAndCarouselType(postManagerMapper.getId(),
					CarouselType.EVENT)
				.orElseThrow(() -> new DaedongException(DaedongStatus.CAROUSEL_NOT_FOUND));

		if (command.isFixed()) {
			postAdminRepository.findFixedPost().ifPresent(PostManagerMapper::unFix);
		}

		postManagerMapper.getPost().update(command.content(), command.title(), command.images());

		postManagerMapper.update(
			command.isFixed(),
			command.isPosted()
		);
		carouselManagerService.toggleCarousel(carouselManager.getId(), command.isCarousel());
		carouselManager.updateBannerImage(command.bannerImage());

		if (!beforePostStatus && command.isPosted()) {
			eventPublisher.publishEvent(
				NoticeEventDto.builder()
					.contentId(postManagerMapper.getId())
					.noticeType(NoticeType.EVENT)
					.build()
			);
		}
	}

	@Override
	public EventResponse getEventPost(final Long managerId) {
		return postAdminRepository.findPostManagerMapperById(managerId)
			.map(manager -> {
				final CarouselManager carouselManager = carouselRepository.findByTargetIdAndCarouselType(
						manager.getId(), CarouselType.EVENT)
					.orElseThrow(() -> new DaedongException(DaedongStatus.CAROUSEL_NOT_FOUND));
				return EventResponse.of(manager, carouselManager);
			})
			.orElseThrow(() -> new DaedongException(DaedongStatus.POST_NOT_FOUND));
	}

	private void validateEventStatus(final EventCommand command) {
		if (!command.isPosted()) {
			if (command.isCarousel() || command.isFixed()) {
				throw new DaedongException(DaedongStatus.INVALID_EVENT_STATUS);
			}
		}
	}
}
