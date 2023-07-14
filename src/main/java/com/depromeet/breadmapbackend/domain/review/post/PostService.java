package com.depromeet.breadmapbackend.domain.review.post;

public interface PostService {

	// post 등록
	void register(String oAuthId);

	// post 조회
	Object getPosts();

	// post 상세 조회
	Object getPost(Long postId);

	// post 추천글 조회
	Object getHotPosts();

	// post 삭제
	void remove(
		String oAuthId,
		Long postId
	);
}
