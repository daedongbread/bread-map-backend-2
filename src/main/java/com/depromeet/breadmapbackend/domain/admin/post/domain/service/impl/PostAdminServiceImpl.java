package com.depromeet.breadmapbackend.domain.admin.post.domain.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.admin.post.domain.PostManagerMapper;
import com.depromeet.breadmapbackend.domain.admin.post.domain.dto.command.EventCommand;
import com.depromeet.breadmapbackend.domain.admin.post.domain.dto.command.UpdateEventOrderCommand;
import com.depromeet.breadmapbackend.domain.admin.post.domain.dto.info.EventCarouselInfo;
import com.depromeet.breadmapbackend.domain.admin.post.domain.dto.info.PostManagerMapperInfo;
import com.depromeet.breadmapbackend.domain.admin.post.domain.repository.PostAdminRepository;
import com.depromeet.breadmapbackend.domain.admin.post.domain.service.PostAdminService;
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

	@Override
	public Page<PostManagerMapperInfo> getEventPosts(final int page) {
		return postAdminRepository.findPostManagerMappers(page)
			.map(PostManagerMapperInfo::new);
	}

	@Transactional
	@Override
	public PostManagerMapper createEventPost(final EventCommand command) {
		validateEventStatus(command);

		final User adminUser = userRepository.findById(adminUserId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
		if (command.isFixed())
			postAdminRepository.findFixedPost().ifPresent(PostManagerMapper::unFix);

		final Post savePost = command.toEventPost(adminUser);

		final PostManagerMapper postManagerMapper = PostManagerMapper.builder()
			.post(command.images() != null ? savePost.addImages(command.images()) : savePost)
			.isPosted(command.isPosted())
			.isCarousel(command.isCarousel())
			.isFixed(command.isFixed())
			.bannerImage(command.bannerImage())
			.carouselOrder(getCarouselOrder(command))
			.build();

		return postAdminRepository.savePostManagerMapper(postManagerMapper);
	}

	@Override
	public boolean canFixEvent() {
		return postAdminRepository.canFixEvent();
	}

	@Override
	@Transactional
	public void updateEventPost(final EventCommand command, final Long managerId) {
		validateEventStatus(command);

		if (command.isFixed())
			postAdminRepository.findFixedPost().ifPresent(PostManagerMapper::unFix);

		final PostManagerMapper postManagerMapper = postAdminRepository.findPostManagerMapperById(managerId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.POST_NOT_FOUND));

		updateCarouselStatus(command, postManagerMapper);

		postManagerMapper.getPost().update(command.content(), command.title(), command.images());

		postManagerMapper.update(
			command.isFixed(),
			command.isPosted(),
			command.isCarousel(),
			command.bannerImage()
		);
	}

	@Transactional
	@Override
	public void updateEventOrder(final List<UpdateEventOrderCommand> commands) {
		final List<PostManagerMapper> carouselPosts = postAdminRepository.findCarouselPosts();
		carouselPosts
			.forEach(post -> commands.stream()
				.filter(command -> command.managerId().equals(post.getId()))
				.findFirst()
				.ifPresent(command -> post.updateCarouselOrder(command.order())));
	}

	@Override
	public List<EventCarouselInfo> getCarousels() {
		return postAdminRepository.findCarouselPosts()
			.stream()
			.map(EventCarouselInfo::of)
			.toList();
	}

	private void validateEventStatus(final EventCommand command) {
		if (!command.isPosted()) {
			if (command.isCarousel() || command.isFixed()) {
				throw new DaedongException(DaedongStatus.INVALID_EVENT_STATUS);
			}
		}
	}

	private Integer getCarouselOrder(final EventCommand command) {
		if (command.isCarousel()) {
			final List<PostManagerMapper> carouselPosts = postAdminRepository.findCarouselPosts();
			if (carouselPosts.size() >= MAX_EVENT_CAROUSEL_COUNT) {
				throw new DaedongException(DaedongStatus.CAROUSEL_POST_COUNT_EXCEEDED);
			}
			return carouselPosts.size() + 1;
		}
		return null;
	}

	private void updateCarouselStatus(final EventCommand command, final PostManagerMapper postManagerMapper) {
		if (command.isCarousel() && !postManagerMapper.isCarousel()) {
			postManagerMapper.updateCarouselOrder(getCarouselOrder(command));
		} else if (!command.isCarousel() && postManagerMapper.isCarousel()) {
			final List<PostManagerMapper> carouselPosts = postAdminRepository.findCarouselPosts();
			boolean flag = false;
			for (PostManagerMapper carouselPost : carouselPosts) {
				if (flag) {
					carouselPost.updateCarouselOrder(carouselPost.getCarouselOrder() - 1);
				}

				if (!flag && carouselPost.getId().equals(postManagerMapper.getId())) {
					carouselPost.updateCarouselOrder(null);
					flag = true;
				}

			}
		}
	}

}
