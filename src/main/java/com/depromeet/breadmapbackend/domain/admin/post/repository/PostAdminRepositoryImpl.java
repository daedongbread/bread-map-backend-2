package com.depromeet.breadmapbackend.domain.admin.post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.depromeet.breadmapbackend.domain.admin.post.domain.PostManagerMapper;
import com.depromeet.breadmapbackend.domain.admin.post.domain.repository.PostAdminRepository;

import lombok.RequiredArgsConstructor;

/**
 * PostAdminRepositoryImpl
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/24
 */
@RequiredArgsConstructor
@Repository
public class PostAdminRepositoryImpl implements PostAdminRepository {
	private final PostManagerMapperJpaRepository postManagerMapperJpaRepository;
	private static final int PAGE_SIZE = 10;

	@Override
	public Page<PostManagerMapper> findPostManagerMappers(final int page) {
		final Pageable pageable = PageRequest.of(page, PAGE_SIZE);
		return postManagerMapperJpaRepository.findPostManagerMappers(pageable);
	}

	@Override
	public PostManagerMapper savePostManagerMapper(final PostManagerMapper postManagerMapper) {
		return postManagerMapperJpaRepository.save(postManagerMapper);
	}

	@Override
	public boolean canFixEvent() {
		return postManagerMapperJpaRepository.getFixedEventCount() <= 0;
	}

	@Override
	public Optional<PostManagerMapper> findFixedPost() {
		return postManagerMapperJpaRepository.findFixedPost();
	}

	@Override
	public List<PostManagerMapper> findCarouselPosts() {
		return postManagerMapperJpaRepository.findCarouselPosts();
	}

	@Override
	public Optional<PostManagerMapper> findPostManagerMapperById(final Long managerId) {
		return postManagerMapperJpaRepository.findPostManagerMapperById(managerId);
	}

}
