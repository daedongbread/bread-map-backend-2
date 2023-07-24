package com.depromeet.breadmapbackend.domain.admin.post.controller.dto.response;

import com.depromeet.breadmapbackend.domain.admin.post.domain.PostManagerMapper;
import com.depromeet.breadmapbackend.domain.post.Post;

/**
 * PostAdminResponse
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/24
 */
public record PostAdminResponse(
	Long id,
	String nickname,
	Long userId,
	String title,
	boolean isFixed,
	boolean isCarousel,
	boolean isPosted,
	String createdAt

) {
	public PostAdminResponse(final PostManagerMapper postManagerMapper) {
		this(
			getPost(postManagerMapper).getId(),
			getPost(postManagerMapper).getUser().getUserInfo().getNickName(),
			getPost(postManagerMapper).getUser().getId(),
			getPost(postManagerMapper).getTitle(),
			postManagerMapper.isFixed(),
			postManagerMapper.isCarousel(),
			postManagerMapper.isPosted(),
			getPost(postManagerMapper).getCreatedAt().toString());
	}

	private static Post getPost(final PostManagerMapper postManagerMapper) {
		return postManagerMapper.getPost();
	}
}
