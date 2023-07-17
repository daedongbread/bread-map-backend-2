package com.depromeet.breadmapbackend.domain.review.post;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.review.ReviewRepository;
import com.depromeet.breadmapbackend.domain.review.post.dto.PostRequestDto;
import com.depromeet.breadmapbackend.domain.review.post.dto.PostResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

	private final ReviewRepository reviewRepository;

	@Override
	public void register(
		PostRequestDto dto,
		String oAuthId
	) {
		// TODO register
	}

	@Override
	public List<PostResponseDto> getPosts() {
		return null;
	}

	@Override
	public PostResponseDto getPost(Long postId) {
		return null;
	}

	@Override
	public List<PostResponseDto> getHotPosts() {
		return null;
	}

	@Override
	public void remove(
		String oAuthId,
		Long postId
	) {

	}
}
