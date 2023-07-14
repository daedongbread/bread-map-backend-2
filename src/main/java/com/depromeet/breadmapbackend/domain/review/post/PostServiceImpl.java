package com.depromeet.breadmapbackend.domain.review.post;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.depromeet.breadmapbackend.domain.review.ReviewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

	private final ReviewRepository reviewRepository;

	@Override
	public void register(String oAuthId) {

	}

	@Override
	public Object getPosts() {
		return null;
	}

	@Override
	public Object getPost(Long postId) {
		return null;
	}

	@Override
	public Object getHotPosts() {
		return null;
	}

	@Override
	public void remove(
		String oAuthId,
		Long postId
	) {

	}
}
