package com.depromeet.breadmapbackend.domain.admin.post.domain.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.admin.post.domain.PostManagerMapper;
import com.depromeet.breadmapbackend.domain.admin.post.domain.dto.command.CreateEventCommand;
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

	@Value("${admin.event.post.user-id}")
	private Long adminUserId;
	private final PostAdminRepository postAdminRepository;
	private final UserRepository userRepository;

	@Override
	public Page<PostManagerMapper> getPosts(final int page) {
		return postAdminRepository.findPostManagerMappers(page);
	}

	@Transactional
	@Override
	public PostManagerMapper createEventPost(final CreateEventCommand command) {
		// TODO : user 테이블 관리자. 임시 저장 // 처리상태 미게시 = 임시저장????
		postAdminRepository.findFixedPost().ifPresent(PostManagerMapper::unFix);
		final User adminUser = userRepository.findById(adminUserId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));

		final Post savePost =
			command.toEventPost(adminUser)
				.addImages(command.images());

		final PostManagerMapper postManagerMapper = PostManagerMapper.builder()
			.post(savePost)
			.isPosted(command.isPosted())
			.isCarousel(command.isCarousel())
			.isFixed(command.isFixed())
			.bannerImage(command.bannerImage())
			.build();

		return postAdminRepository.savePostManagerMapper(postManagerMapper);
	}

	@Override
	public boolean canFixEvent() {
		return postAdminRepository.canFixEvent();
	}
}
