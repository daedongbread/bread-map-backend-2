package com.depromeet.breadmapbackend.domain.review.post;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.review.post.dto.PostRegisterCommand;
import com.depromeet.breadmapbackend.domain.review.post.dto.response.CommunityCardResponse;
import com.depromeet.breadmapbackend.domain.review.post.dto.response.PostResponse;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

	private final PostRepository postRepository;
	private final UserRepository userRepository;

	@Override
	@Transactional
	public void register(
		final PostRegisterCommand command,
		final Long userId
	) {
		final User user = userRepository.findById(userId)
			.orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));

		postRepository.save(command.toEntity(user));
	}

	@Override
	public PageResponseDto<CommunityCardResponse> getPosts() {
		return postRepository.findAllCommunityCards();
	}

	@Override
	public PostResponse getPost(final Long userId, final Long postId) {
		return PostResponse.of(postRepository.findPostBy(postId, userId));
	}

	@Override
	public List<PostResponse> getHotPosts() {
		return null;
	}

	@Override
	@Transactional
	public void remove(
		Long userId,
		Long postId
	) {
		postRepository.deletePostById(postId, userId);
	}
}
